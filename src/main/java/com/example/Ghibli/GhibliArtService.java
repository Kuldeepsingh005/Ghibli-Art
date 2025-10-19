package com.example.Ghibli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;

@Service
public class GhibliArtService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final StabilityAIClient stabilityAIClient;
    private final String apiKey;

    public GhibliArtService(StabilityAIClient stabilityAIClient, @Value("${stability.api.key}") String apiKey) {
        this.stabilityAIClient = stabilityAIClient;
        this.apiKey = apiKey;
        logger.info("GhibliArtService initialized. STABILITY_API_KEY present? {}", apiKey != null && !apiKey.isBlank());
    }

    /**
     * Image -> Image
     */
    public byte[] createGhibliArt(MultipartFile image, String prompt) {
        String finalPrompt = prompt + ", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-xl-1024-v1-0";
        String stylePreset = "anime";

        try {
            logger.debug("Calling Stability API (image->image) engine={}, promptLen={}, fileName={}",
                    engineId, finalPrompt.length(), image == null ? "<null>" : image.getOriginalFilename());

            byte[] result = stabilityAIClient.generateImageFromImage(
                    "Bearer " + apiKey,
                    engineId,
                    image,
                    finalPrompt,
                    stylePreset
            );

            if (result == null || result.length == 0) {
                logger.warn("Stability returned empty body for image->image request");
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from provider");
            }

            logger.info("Stability image->image success: {} bytes", result.length);
            return result;

        } catch (FeignException fe) {
            handleFeignException(fe);
            return null; // Unreachable, but required
        } catch (Exception e) {
            logger.error("Unhandled exception in createGhibliArt", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error generating image");
        }
    }

    /**
     * Text -> Image
     */
    public byte[] createGhibliArtFromText(String prompt, String style) {
        String finalPrompt = prompt + ", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-xl-1024-v1-0";
        String stylePreset = (style == null || style.isBlank() || style.equals("general")) ? "anime" : style.replace("_", "-");

        // Create request payload
        TextToImageRequest requestPayload = new TextToImageRequest(finalPrompt, stylePreset);

        // Optional: customize parameters
        requestPayload.setWidth(1024);
        requestPayload.setHeight(1024);
        requestPayload.setSteps(50);
        requestPayload.setCfg_scale(9.0);

        try {
            logger.debug("Calling Stability API (text->image) engine={}, promptLen={}", engineId, finalPrompt.length());

            byte[] result = stabilityAIClient.generateImageFromText(
                    "Bearer " + apiKey,
                    engineId,
                    requestPayload
            );

            if (result == null || result.length == 0) {
                logger.warn("Stability returned empty body for text->image request");
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from provider");
            }

            logger.info("Stability text->image success: {} bytes", result.length);
            return result;

        } catch (FeignException fe) {
            handleFeignException(fe);
            return null; // Unreachable
        } catch (Exception e) {
            logger.error("Unhandled exception in createGhibliArtFromText", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error generating image");
        }
    }

    /**
     * Common FeignException handler
     */
    private void handleFeignException(FeignException fe) {
        String body = safeFeignContent(fe);
        logger.error("FeignException during API call. status={}, body={}", fe.status(), body, fe);

        if (fe.status() == 404) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Engine not found: " + body);
        } else if (fe.status() == 401) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provider auth error: " + body);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Provider error: " + body);
        }
    }

    /**
     * Safely get content from FeignException
     */
    private String safeFeignContent(FeignException fe) {
        try {
            return fe.contentUTF8();
        } catch (Exception ex) {
            return "<unreadable body>";
        }
    }
}







