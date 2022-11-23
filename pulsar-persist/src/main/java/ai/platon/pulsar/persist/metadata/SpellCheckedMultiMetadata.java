/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.platon.pulsar.persist.metadata;

import ai.platon.pulsar.common.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A decorator to MultiMetadata that adds spellchecking capabilities to property
 * names. Currently used spelling vocabulary contains just the httpheaders from
 * {@link ai.platon.pulsar.common.HttpHeaders} class.
 *
 * @author vincent
 * @version $Id: $Id
 */
public class SpellCheckedMultiMetadata extends MultiMetadata {

    /**
     * Treshold divider.
     * <p>
     * <code>threshold = searched.length() / TRESHOLD_DIVIDER;</code>
     */
    private static final int TRESHOLD_DIVIDER = 3;

    /**
     * Normalized name to name mapping.
     */
    private final static Map<String, String> NAMES_IDX = new HashMap<>();

    /**
     * Array holding map keys.
     */
    private static String[] normalized = null;

    static {
        // Uses following array to fill the metanames index and the
        // metanames list.
        Class<?>[] spellthese = {HttpHeaders.class, CrawlVariables.class};

        for (Class<?> spellCheckedNames : spellthese) {
            for (Field field : spellCheckedNames.getFields()) {
                int mods = field.getModifiers();
                if (Modifier.isFinal(mods) && Modifier.isPublic(mods)
                        && Modifier.isStatic(mods) && field.getType().equals(String.class)) {
                    try {
                        String val = (String) field.get(null);
                        NAMES_IDX.put(normalize(val), val);
                    } catch (Exception e) {
                        // Simply ignore...
                    }
                }
            }
        }
        normalized = NAMES_IDX.keySet().toArray(new String[0]);
    }

    /**
     * Normalizes String.
     *
     * @param str the string to normalize
     * @return normalized String
     */
    private static String normalize(String str) {
        char c;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (Character.isLetter(c)) {
                buf.append(Character.toLowerCase(c));
            }
        }
        return buf.toString();
    }

    /**
     * Get the normalized name of metadata attribute name. This method tries to
     * find a well-known metadata name (one of the metadata names defined in this
     * class) that matches the specified name. The matching is error tolerent. For
     * instance,
     * <ul>
     * <li>content-type gives Content-Type</li>
     * <li>CoNtEntType gives Content-Type</li>
     * <li>ConTnTtYpe gives Content-Type</li>
     * </ul>
     * If no matching with a well-known metadata name is found, then the original
     * name is returned.
     *
     * @param name Name to normalize
     * @return normalized name
     */
    public static String getNormalizedName(String name) {
        String searched = normalize(name);
        String value = NAMES_IDX.get(searched);

        if ((value == null) && (normalized != null)) {
            int threshold = searched.length() / TRESHOLD_DIVIDER;
            for (int i = 0; i < normalized.length && value == null; i++) {
                if (StringUtils.getLevenshteinDistance(searched, normalized[i]) < threshold) {
                    value = NAMES_IDX.get(normalized[i]);
                }
            }
        }
        return (value != null) ? value : name;
    }

    /** {@inheritDoc} */
    @Override
    public void removeAll(String name) {
        super.removeAll(getNormalizedName(name));
    }

    /** {@inheritDoc} */
    @Override
    public void put(String name, final String value) {
        super.put(getNormalizedName(name), value);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> getValues(String name) {
        return super.getValues(getNormalizedName(name));
    }

    /** {@inheritDoc} */
    @Override
    public String get(String name) {
        return super.get(getNormalizedName(name));
    }

    /** {@inheritDoc} */
    @Override
    public void set(String name, final String value) {
        super.set(getNormalizedName(name), value);
    }
}