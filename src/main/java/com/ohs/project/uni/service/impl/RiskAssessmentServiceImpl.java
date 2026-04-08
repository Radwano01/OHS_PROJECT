package com.ohs.project.uni.service.impl;

import com.ohs.project.uni.dto.RiskAssessmentDTO;
import com.ohs.project.uni.dto.RiskAssessmentDetailsDTO;
import com.ohs.project.uni.entity.RiskAssessment;
import com.ohs.project.uni.entity.enums.AssetType;
import com.ohs.project.uni.entity.enums.Hazard;
import com.ohs.project.uni.entity.enums.Probability;
import com.ohs.project.uni.entity.enums.Severity;
import com.ohs.project.uni.repository.ExistingControlRepository;
import com.ohs.project.uni.repository.RiskAssessmentRepository;
import com.ohs.project.uni.service.RiskAssessmentService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {


    private final RiskAssessmentRepository riskAssessmentRepository;
    private final ExistingControlRepository existingControlRepository;

    @Autowired
    public RiskAssessmentServiceImpl(RiskAssessmentRepository riskAssessmentRepository,
                                     ExistingControlRepository existingControlRepository) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.existingControlRepository = existingControlRepository;
    }


    @Override
    public String save(RiskAssessmentDTO riskAssessmentDTO, String userId) {
        Probability probability = getProbability(riskAssessmentDTO.getUsageFrequency(),
                riskAssessmentDTO.getExposureLevel(),
                riskAssessmentDTO.getIncidentHistory()
        );
        Severity severity = getSeverity(riskAssessmentDTO.getHumanImpact(),
                riskAssessmentDTO.getBusinessImpact(),
                riskAssessmentDTO.getDamageImpact()
        );
        int riskScore = (probability.ordinal() + 1) * (severity.ordinal() + 1);
        RiskAssessment riskAssessment = new RiskAssessment(
                userId,
                riskAssessmentDTO.getAssetName(),
                riskAssessmentDTO.getAssetType(),
                riskAssessmentDTO.getAssetCategory(),
                getHazards(riskAssessmentDTO.getAssetType()),
                probability,
                severity,
                riskScore,
                getExistingControl(riskAssessmentDTO.getAssetType())
        );
        riskAssessmentRepository.save(riskAssessment);
        return riskAssessment.getId();
    }

    @Override
    public byte[] getRiskAssessment(String id) {
        RiskAssessment riskAssessment = riskAssessmentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Risk Assessment not found"));
        return generateRiskReport(riskAssessment);
    }

    @Override
    public List<RiskAssessmentDetailsDTO> getUserHistory(String userId) {
        List<RiskAssessment> assets = riskAssessmentRepository.findByUserId(userId);
        return assets.stream()
                .map(ra -> new RiskAssessmentDetailsDTO(
                        ra.getId(),
                        ra.getAssetName(),
                        ra.getAssetType(),
                        ra.getAssetCategory(),
                        ra.getHazard(),
                        ra.getProbability(),
                        ra.getSeverity(),
                        ra.getRiskScore(),
                        ra.getCreatedAt()
                ))
                .toList();
    }

    private List<Hazard> getHazards(AssetType assetType) {
        return switch (assetType) {
            case MACHINE -> List.of(
                    Hazard.MECHANICAL_FAILURE,
                    Hazard.OVERHEATING,
                    Hazard.FIRE,
                    Hazard.ELECTRICAL_FAULT
            );
            case BUILDING -> List.of(
                    Hazard.FIRE,
                    Hazard.ELECTRICAL_FAULT,
                    Hazard.LEAKAGE
            );
            case VEHICLE -> List.of(
                    Hazard.MECHANICAL_FAILURE,
                    Hazard.FIRE,
                    Hazard.LEAKAGE
            );
            case EQUIPMENT -> List.of(
                    Hazard.MECHANICAL_FAILURE,
                    Hazard.ELECTRICAL_FAULT,
                    Hazard.OVERHEATING
            );
            case STORAGE -> List.of(
                    Hazard.FIRE,
                    Hazard.LEAKAGE
            );
            case SERVER -> List.of(
                    Hazard.CYBER_ATTACK,
                    Hazard.DATA_LOSS,
                    Hazard.SYSTEM_FAILURE,
                    Hazard.POWER_OUTAGE
            );
            case DATABASE -> List.of(
                    Hazard.DATA_LOSS,
                    Hazard.UNAUTHORIZED_ACCESS,
                    Hazard.CYBER_ATTACK
            );
            case WEBSITE -> List.of(
                    Hazard.CYBER_ATTACK,
                    Hazard.SYSTEM_FAILURE,
                    Hazard.UNAUTHORIZED_ACCESS
            );
            case APPLICATION -> List.of(
                    Hazard.SYSTEM_FAILURE,
                    Hazard.DATA_LOSS
            );
            case NETWORK_DEVICE -> List.of(
                    Hazard.NETWORK_FAILURE,
                    Hazard.CYBER_ATTACK
            );
            case WORKER, TECHNICIAN, DRIVER -> List.of(
                    Hazard.HUMAN_ERROR,
                    Hazard.FATIGUE,
                    Hazard.LACK_OF_TRAINING
            );
            case MANAGER -> List.of(
                    Hazard.HUMAN_ERROR,
                    Hazard.COMMUNICATION_BREAKDOWN
            );
            case PRODUCTION_PROCESS, MAINTENANCE_PROCESS -> List.of(
                    Hazard.PROCESS_FAILURE,
                    Hazard.DELAY
            );
            case DELIVERY_PROCESS -> List.of(
                    Hazard.DELAY,
                    Hazard.SUPPLY_FAILURE
            );
            case INVENTORY_PROCESS -> List.of(
                    Hazard.PROCESS_FAILURE,
                    Hazard.DATA_LOSS
            );
            case PAYMENT_PROCESS -> List.of(
                    Hazard.SYSTEM_FAILURE,
                    Hazard.UNAUTHORIZED_ACCESS
            );
            default -> List.of(
                    Hazard.PROCESS_FAILURE,
                    Hazard.HUMAN_ERROR,
                    Hazard.SYSTEM_FAILURE
            );
        };
    }

    private Probability getProbability(int usageFrequency,
                                       int exposureLevel,
                                       int incidentHistory) {
        int avg = (usageFrequency + exposureLevel + incidentHistory) / 3;
        return Probability.getValue(Math.max(1, Math.min(avg, 5)));
    }

    private Severity getSeverity(int humanImpact,
                                 int businessImpact,
                                 int damageImpact){
        return Severity.getValue(Math.max(humanImpact, Math.max(businessImpact, damageImpact)));
    }

    private List<String> getExistingControl(AssetType assetType) {
        return existingControlRepository.findExistingControlByAssetType(assetType);
    }

    private byte[] generateRiskReport(RiskAssessment risk) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                float margin = 50;
                float y = 780;
                float leading = 18;

                // Title
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 20);
                content.newLineAtOffset(margin, y);
                content.showText("Risk Assessment Report");
                content.endText();

                y -= 30;

                // Subtitle / description
                String description =
                        "This document provides a professional summary of the identified risks, " +
                                "their assessed probability and severity, and the existing control measures " +
                                "currently in place to reduce the likelihood of incidents and minimize impact.";

                y = writeWrappedText(content, description, margin, y, 12, PDType1Font.HELVETICA, 500, 16);

                y -= 15;

                // Divider
                drawLine(content, margin, y, 545, y);
                y -= 25;

                // Basic information
                y = writeLabelValue(content, "Report ID", safe(risk.getId()), margin, y);
                y = writeLabelValue(content, "Asset Name", safe(risk.getAssetName()), margin, y);
                y = writeLabelValue(content, "Asset Type", formatEnum(risk.getAssetType()), margin, y);
                y = writeLabelValue(content, "Asset Category", formatEnum(risk.getAssetCategory()), margin, y);
                y = writeLabelValue(content, "Probability", formatEnum(risk.getProbability()), margin, y);
                y = writeLabelValue(content, "Severity", formatEnum(risk.getSeverity()), margin, y);
                y = writeLabelValue(content, "Risk Score", String.valueOf(risk.getRiskScore()), margin, y);
                y = writeLabelValue(content, "Created At", formatDate(risk.getCreatedAt()), margin, y);

                y -= 10;

                // Hazards section
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(margin, y);
                content.showText("Identified Hazards");
                content.endText();

                y -= 20;

                List<Hazard> hazards = risk.getHazard() != null ? risk.getHazard() : new ArrayList<>();
                if (hazards.isEmpty()) {
                    y = writeBullet(content, "No hazards recorded.", margin + 10, y);
                } else {
                    for (Hazard hazard : hazards) {
                        y = writeBullet(content, formatEnum(hazard), margin + 10, y);
                    }
                }

                y -= 10;

                // Existing controls section
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(margin, y);
                content.showText("Existing Control Measures");
                content.endText();

                y -= 20;

                List<String> parsedControls = extractExistingControls(risk.getExistingControls());

                if (parsedControls.isEmpty()) {
                    y = writeBullet(content, "No existing control measures recorded.", margin + 10, y);
                } else {
                    for (String control : parsedControls) {
                        y = writeBullet(content, control, margin + 10, y);
                    }
                }

                y -= 20;

                // Final note
                String note =
                        "Note: This report reflects the available assessment data at the time of creation. " +
                                "Risk levels should be reviewed regularly and updated whenever operational conditions, " +
                                "equipment status, or workplace controls change.";

                writeWrappedText(content, note, margin, y, 11, PDType1Font.HELVETICA_OBLIQUE, 500, 15);
            }

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private float writeLabelValue(PDPageContentStream content,
                                  String label,
                                  String value,
                                  float x,
                                  float y) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(x, y);
        content.showText(label + ": ");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(x + 100, y);
        content.showText(safe(value));
        content.endText();

        return y - 20;
    }

    private float writeBullet(PDPageContentStream content,
                              String text,
                              float x,
                              float y) throws IOException {
        List<String> lines = wrapText(text, 80);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(x, y);
        content.showText("• " + lines.get(0));
        content.endText();

        float currentY = y - 16;

        for (int i = 1; i < lines.size(); i++) {
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(x + 12, currentY);
            content.showText(lines.get(i));
            content.endText();
            currentY -= 16;
        }

        return currentY;
    }

    private float writeWrappedText(PDPageContentStream content,
                                   String text,
                                   float x,
                                   float y,
                                   int fontSize,
                                   PDType1Font font,
                                   float width,
                                   float lineHeight) throws IOException {
        List<String> lines = wrapTextByLength(text, 95);

        float currentY = y;
        for (String line : lines) {
            content.beginText();
            content.setFont(font, fontSize);
            content.newLineAtOffset(x, currentY);
            content.showText(line);
            content.endText();
            currentY -= lineHeight;
        }
        return currentY;
    }

    private void drawLine(PDPageContentStream content,
                          float startX,
                          float startY,
                          float endX,
                          float endY) throws IOException {
        content.moveTo(startX, startY);
        content.lineTo(endX, endY);
        content.stroke();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy, HH:mm").format(date);
    }

    private String formatEnum(Object value) {
        if (value == null) {
            return "-";
        }

        String text = String.valueOf(value).replace("_", " ").toLowerCase();
        String[] words = text.split(" ");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    private List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("-");
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private List<String> wrapTextByLength(String text, int maxLineLength) {
        return wrapText(text, maxLineLength);
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    private List<String> extractExistingControls(List<String> existingControlsJson) {
        List<String> controls = new ArrayList<>();

        if (existingControlsJson == null || existingControlsJson.isEmpty()) {
            return controls;
        }

        Pattern pattern = Pattern.compile("\"ExistingControl\"\\s*:\\s*\\[(.*?)]");

        for (String json : existingControlsJson) {
            if (json == null || json.isBlank()) {
                continue;
            }

            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String arrayContent = matcher.group(1);
                String[] items = arrayContent.split(",");

                for (String item : items) {
                    String cleaned = item.replace("\"", "").trim();
                    if (!cleaned.isEmpty()) {
                        controls.add(cleaned);
                    }
                }
            }
        }

        return controls;
    }
}
