package com.cortis.business.model;

import java.util.Locale;

/** Closed list of real V5–V9 database tables. Never interpolate untrusted table names. */
public enum CatalogKind {
    TOOLS("tools", "tool", true),
    HOLDERS("holders", "holder", true),
    INTERFACES("interfaces", "interface", true),
    MACHINES("machines", "machine", false),
    MATERIALS("materials", "material", true),
    ACCESSORIES("accessories", "accessory", true),
    PARTS("parts", "part", true);

    private final String slug;
    private final String noun;
    private final boolean stocked;

    CatalogKind(String slug, String noun, boolean stocked) {
        this.slug = slug;
        this.noun = noun;
        this.stocked = stocked;
    }
    public String slug() { return slug; }
    public String noun() { return noun; }
    public boolean stocked() { return stocked; }
    public String masterTable() { return "specification." + noun + "_master"; }
    public String typeTable() { return "specification." + noun + "_type"; }
    public String inventoryTable() { return "company." + noun + "_inventory"; }
    public String codeColumn() { return noun + "_code"; }
    public String masterFkColumn() { return noun + "_master_id"; }

    public static CatalogKind parse(String value) {
        if (value != null) {
            for (CatalogKind kind : values()) {
                if (kind.slug.equals(value.toLowerCase(Locale.ROOT))) return kind;
            }
        }
        throw new BusinessException(400, "INVALID_KIND", "Allowed kinds: tools, holders, interfaces, machines, materials, accessories, parts");
    }
}
