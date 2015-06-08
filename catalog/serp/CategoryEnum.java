/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

/**
 * <p>Titre : CategoryEnum.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public enum CategoryEnum {

    /** The Category 1 .*/
    CATEGORY_1("category1", "one"),

    /** The Category 2 .*/
    CATEGORY_2("category2", "two"),

    /** The Category 3 .*/
    CATEGORY_3("category3", "three"),

    /** The Category 4 .*/
    CATEGORY_4("category4", "four"),

    /** The Category 5 .*/
    CATEGORY_5("category5", "five");

    /** The Category code . */
    private final String code;

    /** The Category name . */
    private final String name;

    /**
     * Private constructor for category code and name.
     * @param code the category's code
     * @param name the day's name
     */
    private CategoryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Getter.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter.
     * @return the code.
     */
    public String getCode() {
        return code;
    }
}
