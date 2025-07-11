package org.esupportail.esupagape.entity.enums.enquete;

public enum CodSco {
    _1("1"),
    _2("2"),
    _3("3"),
    _4("4"),
    _5("5"),
    _6("6+"),
    NSP("NSP");

    private final String code;

    CodSco(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}