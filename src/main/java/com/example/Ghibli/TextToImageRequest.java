package com.example.Ghibli;

import java.util.List;

public class TextToImageRequest {

    private List<TextPrompt> text_prompts;
    private double cfg_scale = 7;
    private int height = 512;
    private int width = 768;
    private int samples = 1;
    private int steps = 30;
    private String style_preset;

    // Inner class for the text prompts
    public static class TextPrompt {
        private String text;

        public TextPrompt(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // Constructor
    public TextToImageRequest(String text, String style) {
        this.text_prompts = List.of(new TextPrompt(text));
        this.style_preset = style;
    }

    // Getters
    public List<TextPrompt> getText_prompts() { return text_prompts; }
    public double getCfg_scale() { return cfg_scale; }
    public int getHeight() { return height; }
    public int getWidth() { return width; }
    public int getSamples() { return samples; }
    public int getSteps() { return steps; }
    public String getStyle_preset() { return style_preset; }

    // Setters (needed to avoid compilation errors)
    public void setCfg_scale(double cfg_scale) { this.cfg_scale = cfg_scale; }
    public void setHeight(int height) { this.height = height; }
    public void setWidth(int width) { this.width = width; }
    public void setSteps(int steps) { this.steps = steps; }
    public void setSamples(int samples) { this.samples = samples; }
    public void setStyle_preset(String style_preset) { this.style_preset = style_preset; }
    public void setText_prompts(List<TextPrompt> text_prompts) { this.text_prompts = text_prompts; }
}

