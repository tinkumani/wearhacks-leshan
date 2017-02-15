/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObserveSpecTest {

    ObserveSpec spec;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void serialize_cancel_param() {
        this.spec = new ObserveSpec.Builder().cancel().build();
        Assert.assertEquals("cancel", this.spec.toQueryParams()[0]);
    }

    @Test
    public void serialize_step_param() {
        Float step = 12.1f;
        this.spec = new ObserveSpec.Builder().step(step).build();
        Assert.assertEquals(String.format("st=%s", step), this.spec.toQueryParams()[0]);
    }

    @Test
    public void serialize_greaterThan_param() {
        Float threshold = 12.1f;
        this.spec = new ObserveSpec.Builder().greaterThan(threshold).build();
        Assert.assertEquals(String.format("gt=%s", threshold), this.spec.toQueryParams()[0]);
    }

    @Test
    public void serialize_lessThan_param() {
        Float threshold = 12.1f;
        this.spec = new ObserveSpec.Builder().lessThan(threshold).build();
        Assert.assertEquals(String.format("lt=%s", threshold), this.spec.toQueryParams()[0]);
    }

    @Test
    public void serialize_minPeriod_param() {
        int seconds = 30;
        this.spec = new ObserveSpec.Builder().minPeriod(seconds).build();
        Assert.assertEquals(String.format("pmin=%s", seconds), this.spec.toQueryParams()[0]);
    }

    @Test
    public void serialize_maxPeriod_param() {
        int seconds = 60;
        this.spec = new ObserveSpec.Builder().maxPeriod(seconds).build();
        Assert.assertEquals(String.format("pmax=%s", seconds), this.spec.toQueryParams()[0]);
    }

    @Test(expected = IllegalStateException.class)
    public void minPeriod_bigger_than_maxPeriod() {
        new ObserveSpec.Builder().minPeriod(5).maxPeriod(2).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_with_invalid_format() {
        ObserveSpec.parse(Arrays.asList("a=b=c"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_with_invalid_key() {
        ObserveSpec.parse(Arrays.asList("a=b"));
    }

    @Test
    public void parse_cancel() {
        assertSameSpecs(new ObserveSpec.Builder().cancel().build(), "cancel");
    }

    @Test
    public void parse_greater_than_6() {
        assertSameSpecs(new ObserveSpec.Builder().greaterThan(6).build(), "gt=6.0");
    }

    @Test
    public void parse_greater_than_8() {
        assertSameSpecs(new ObserveSpec.Builder().greaterThan(8).build(), "gt=8.0");
    }

    @Test
    public void parse_less_than_8() {
        assertSameSpecs(new ObserveSpec.Builder().lessThan(8).build(), "lt=8.0");
    }

    @Test
    public void parse_less_than_8_and_greater_than_14() {
        assertSameSpecs(new ObserveSpec.Builder().greaterThan(14).lessThan(8).build(), "lt=8.0", "gt=14.0");
    }

    @Test
    public void parse_all_the_things() {
        ObserveSpec spec = new ObserveSpec.Builder().greaterThan(14).lessThan(8).minPeriod(5).maxPeriod(10).step(1)
                .build();
        assertSameSpecs(spec, "gt=14.0", "lt=8.0", "pmin=5", "pmax=10", "st=1.0");
    }

    @Test(expected = IllegalStateException.class)
    public void parse_out_of_order_pmin_pmax() {
        ObserveSpec.parse(Arrays.asList("pmin=50", "pmax=10"));
    }

    private void assertSameSpecs(ObserveSpec expected, String... inputs) {
        List<String> queries = Arrays.asList(inputs);
        ObserveSpec actual = ObserveSpec.parse(queries);
        assertSameSpecs(expected, actual);
    }

    private void assertSameSpecs(ObserveSpec expected, ObserveSpec actual) {
        assertEquals(expected.toString(), actual.toString());
    }
}
