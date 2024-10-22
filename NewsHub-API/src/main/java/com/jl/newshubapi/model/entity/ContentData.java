package com.jl.newshubapi.model.entity;

import java.util.List;

public class ContentData {
    private List<ContentPart> contents;

    public List<ContentPart> getContents() {
        return contents;
    }

    public void setContents(List<ContentPart> contents) {
        this.contents = contents;
    }

    public static class ContentPart {
        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
