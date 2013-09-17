/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.plugin.service.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.craftercms.plugin.PluginManager.PluginState;
import org.craftercms.plugin.service.impl.PluginStatusServiceImpl;


import org.junit.Before;
import org.junit.After;
import org.junit.Test;


/**
 * @author David Escalante López
 */
public class PluginStatusServiceTest {

    // <editor-fold defaultstate="collapsed" desc="Constants">

    private static String TEST_PROPERTIES_PATH = "test-plugin-manager.properties";

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attributes">

    PluginStatusService pluginStatusService = null;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Set Up">

    @Before
    public void setUp() {
        pluginStatusService = new PluginStatusServiceImpl(TEST_PROPERTIES_PATH);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Tests">

    @Test
    public void addOneTest() {
        //arrange
        PluginState testState = PluginState.DISABLED;

        //act
        boolean result = pluginStatusService.addOne("aaa", testState);

        //assert
        assertTrue(result);
        assertExists("aaa", testState);

        //arrange
        testState = PluginState.ENABLED_ACTIVE;

        //act
        result = pluginStatusService.addOne("aaa", testState);

        //assert
        assertFalse(result);
        assertExists("aaa", PluginState.DISABLED);
    }

    @Test
    public void addListTest() {
        //arrange
        boolean result;
        PluginState testState = PluginState.ENABLED_INACTIVE;
        List<String> plugins = new ArrayList<>();
        plugins.add("aaa");
        plugins.add("bbb");
        plugins.add("ccc");
        plugins.add("ddd");
        plugins.add("eee");
        plugins.add("fff");

        //act
        result = pluginStatusService.addList(plugins, testState);

        //assert
        assertTrue(result);
        for (String pluginId : plugins) {
            assertExists(pluginId, testState);
        }

        //arrange
        testState = PluginState.ENABLED_ACTIVE;
        List<String> duplicatedPlugins = new ArrayList<>();
        duplicatedPlugins.add("bbb");
        duplicatedPlugins.add("ccc");
        duplicatedPlugins.add("ddd");
        duplicatedPlugins.add("eee");

        //act
        result = pluginStatusService.addList(duplicatedPlugins, testState);

        //assert
        assertFalse(result);
        for (String pluginId : duplicatedPlugins) {
            assertExists(pluginId, PluginState.ENABLED_INACTIVE);
        }

    }

    @Test
    public void getStatusByIdTest() {
        //arrange
        PluginState testState = PluginState.DISABLED;
        pluginStatusService.addOne("aaa", testState);

        //act
        PluginState pluginState = pluginStatusService.getStatusById("aaa");

        //assert
        assertEquals(testState, pluginState);

        //act
        pluginState = pluginStatusService.getStatusById("bbb");

        //assert
        assertNull(pluginState);
    }

    @Test
    public void getAllByStatusTest() {
        //arrange
        PluginState testState = PluginState.DISABLED;
        List<String> pluginsTestDisabled = new ArrayList<>();
        pluginsTestDisabled.add("aaa");
        pluginsTestDisabled.add("bbb");
        pluginsTestDisabled.add("ccc");
        pluginStatusService.addList(pluginsTestDisabled, testState);

        PluginState testStateSecondCase = PluginState.ENABLED_ACTIVE;
        pluginStatusService.addOne("ddd", testStateSecondCase);
        pluginStatusService.addOne("eee", testStateSecondCase);
        pluginStatusService.addOne("fff", testStateSecondCase);


        //act
        List<String> plugIds = pluginStatusService.getAllByStatus(testState);

        //assert
        assertArrayEquals(pluginsTestDisabled.toArray(), plugIds.toArray());

        //arrange
        PluginState testStateThirdCase = PluginState.ENABLED_INACTIVE;
        String[] emptyArray = {};

        //act
        plugIds = pluginStatusService.getAllByStatus(testStateThirdCase);

        //assert
        assertArrayEquals(emptyArray, plugIds.toArray());
    }

    @Test
    public void getAllTest() {
        //arrange
        pluginStatusService.addOne("aaa", PluginState.ENABLED_ACTIVE);
        pluginStatusService.addOne("bbb", PluginState.ENABLED_INACTIVE);
        pluginStatusService.addOne("ccc", PluginState.DISABLED);
        List<String> plugins = new ArrayList<>();
        plugins.add("aaa");
        plugins.add("bbb");
        plugins.add("ccc");

        //act
        List<String> plugIds = pluginStatusService.getAll();

        //assert
        assertArrayEquals(plugins.toArray(), plugIds.toArray());
    }

    @Test
    public void updateOneTest() {
        //arrange
        boolean result;
        PluginState testState = PluginState.DISABLED;
        pluginStatusService.addOne("aaa", PluginState.ENABLED_ACTIVE);

        //act
        result = pluginStatusService.updateOne("aaa", testState);
        PluginState pluginState = pluginStatusService.getStatusById("aaa");

        //assert
        assertTrue(result);
        assertEquals(testState, pluginState);

        //act
        result = pluginStatusService.updateOne("bbb", testState);

        //assert
        assertFalse(result);
    }

    @Test
    public void updateListTest() {
        //arrange
        boolean result;
        PluginState testState = PluginState.DISABLED;
        PluginState testStateSecondCase = PluginState.ENABLED_ACTIVE;
        List<String> pluginsTestUpdate = new ArrayList<>();
        pluginsTestUpdate.add("aaa");
        pluginsTestUpdate.add("bbb");
        pluginsTestUpdate.add("ccc");

        pluginStatusService.addList(pluginsTestUpdate, testState);
        pluginStatusService.addOne("ddd", testState);
        pluginStatusService.addOne("eee", testState);
        pluginStatusService.addOne("fff", testState);

        //act
        result = pluginStatusService.updateList(pluginsTestUpdate, testStateSecondCase);
        List<String> plugIds = pluginStatusService.getAllByStatus(testStateSecondCase);

        //assert
        assertTrue(result);
        assertArrayEquals(pluginsTestUpdate.toArray(), plugIds.toArray());

        //arrange
        List<String> pluginsTestNotFoundUpdate = new ArrayList<>();
        pluginsTestNotFoundUpdate.add("ggg");
        pluginsTestNotFoundUpdate.add("hhh");
        pluginsTestNotFoundUpdate.add("iii");

        result = pluginStatusService.updateList(pluginsTestNotFoundUpdate, testStateSecondCase);

        //assert
        assertFalse(result);
    }

    @Test
    public void updateAllTest() {
        //arrange
        boolean result;
        PluginState testState = PluginState.DISABLED;
        pluginStatusService.addOne("aaa", PluginState.ENABLED_ACTIVE);
        pluginStatusService.addOne("bbb", PluginState.ENABLED_INACTIVE);
        pluginStatusService.addOne("ccc", PluginState.DISABLED);
        List<String> plugins = new ArrayList<>();
        plugins.add("aaa");
        plugins.add("bbb");
        plugins.add("ccc");

        //act
        result = pluginStatusService.updateAll(testState);
        List<String> plugIds = pluginStatusService.getAllByStatus(testState);


        //assert
        assertTrue(result);
        assertArrayEquals(plugins.toArray(), plugIds.toArray());


        //arrange
        cleanUp();

        //act
        result = pluginStatusService.updateAll(testState);

        //assert
        assertFalse(result);
    }

    @Test
    public void deleteTest() {
        //arrange
        boolean result;
        pluginStatusService.addOne("aaa", PluginState.ENABLED_ACTIVE);

        //act
        result = pluginStatusService.delete("aaa");

        //assert
        assertTrue(result);
        assertNotExists("aaa");

        //arrange
        PluginState testState = PluginState.DISABLED;
        pluginStatusService.addOne("bbb", testState);

        //act
        result = pluginStatusService.delete("ccc");

        //assert
        assertFalse(result);
        assertExists("bbb", testState);

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Tear Down">

    @After
    public void tearDown() {

        Properties properties = new Properties();

        try {
            properties.load(PluginStatusServiceImpl.class.getClassLoader().getResourceAsStream(TEST_PROPERTIES_PATH));
            File file = new File(properties.getProperty("plugin.status.file"));
            if (!file.delete()) {
                fail("Error deleting the test XML");
            }
        } catch (IOException e) {
            fail("Error deleting the test XML");
        }

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Internal Functionality">

    private void cleanUp() {
        List<String> pluginIds = pluginStatusService.getAll();
        for (String pluginId : pluginIds) {
            pluginStatusService.delete(pluginId);
        }
    }

    private void assertExists(final String pluginId, PluginState expectedState) {
        PluginState state = pluginStatusService.getStatusById(pluginId);
        if (state == null) {
            fail(String.format("Plugin id=%s does not exit", pluginId));
        } else if (state != expectedState) {
            fail(String.format("Plugin {id=%s, state=%s} has different expected state (%s)", pluginId, state,
                expectedState));
        }
    }

    private void assertNotExists(final String pluginId) {
        PluginState state = pluginStatusService.getStatusById(pluginId);
        if (state != null) {
            fail(String.format("Plugin id=%s exits", pluginId));
        }
    }

    // </editor-fold>
}
