package com.snansidansi.tag;

import java.util.List;

/**
 * A simple class that is used to group tag data from the {@link TagManager}.
 * This class contains the fields:
 * <ul>
 *     <li>{@code name}: Tag name as string.</li>
 *     <li>{@code color}: Tag color as string.</li>
 *     <li>{@code content}: Tag content as {@code List<Integer>}.</li>
 * </ul>
 */
public class Tag {
    public String name;
    public String color;
    public List<Integer> content;

    /**
     * Constructs a new Tag object.
     *
     * @param name    Name of the tag as string.
     * @param color   Color of the tag as string.
     * @param content Content of the tag as {@code List<Integer>}.
     */
    public Tag(String name, String color, List<Integer> content) {
        this.name = name;
        this.color = color;
        this.content = content;
    }

    /**
     * Check if a {@link Object} is equal to this {@code Tag} object. If the given {@link Object} is a {@code Tag} then
     * the name and color fields of the {@code Tag} instances get compared.
     *
     * @param object Any object.
     * @return Boolean value if both {@code Objects} are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof Tag tagObject) {
            return tagObject.name.equals(this.name) &&
                    tagObject.color.equals(this.color);
        }
        return false;
    }
}
