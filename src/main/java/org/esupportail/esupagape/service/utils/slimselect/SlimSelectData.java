package org.esupportail.esupagape.service.utils.slimselect;

public class SlimSelectData {

    String text;
    String value;

    public SlimSelectData(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
