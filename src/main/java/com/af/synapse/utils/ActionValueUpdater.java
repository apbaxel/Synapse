/**
 * Author: Andrei F.
 *
 * This file is part of the "Synapse" software and is licensed under
 * under the Microsoft Reference Source License (MS-RSL).
 *
 * Please see the attached LICENSE.txt for the full license.
 */

package com.af.synapse.utils;

import android.view.Menu;
import android.view.MenuItem;

import com.af.synapse.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrei on 04/09/13.
 */
public class ActionValueUpdater {
    private static ArrayList<ArrayList<ActionValueClient>> perpetuals = null;
    private static ArrayList<ActionValueClient> registrees = new ArrayList<ActionValueClient>();
    private static MenuItem applyButton;
    private static MenuItem cancelButton;
    private static boolean blocked = false;

    public static void registerPerpetual(ActionValueClient element, int sectionNumber) {
        if (perpetuals == null) {
            perpetuals = new ArrayList<ArrayList<ActionValueClient>>();
            for (Object o : Utils.configSections)
                perpetuals.add(new ArrayList<ActionValueClient>());
        }

        ArrayList<ActionValueClient> sectionList = perpetuals.get(sectionNumber);
        sectionList.add(element);
    }

    public static void registerElement(ActionValueClient element) {
        if (blocked)
            return;

        registrees.add(element);
        refreshButtons();
    }

    public static void removeElement(ActionValueClient element) {
        if (blocked)
            return;

        registrees.remove(element);
        refreshButtons();
    }

    public static boolean isRegistered(ActionValueClient element) {
        return registrees.contains(element);
    }

    private static void changeElements(boolean commit) {
        blocked = true;

        for (ActionValueClient element : registrees) {
            if (commit)
                element.commitValue();
            else
                element.cancelValue();
        }

        registrees.clear();
        blocked = false;

        refreshButtons();
    }

    public static void refreshButtons() {
        if (applyButton == null || cancelButton == null || Utils.appStart)
            return;

        Utils.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (registrees.isEmpty()) {
                    applyButton.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                    cancelButton.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                    applyButton.setVisible(false);
                    cancelButton.setVisible(false);
                } else {
                    applyButton.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
                    cancelButton.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
                    applyButton.setVisible(true);
                    cancelButton.setVisible(true);
                }
            }
        });
    }

    public static void applyElements() {
        changeElements(true);
    }

    public static void cancelElements() {
        changeElements(false);
    }

    public static void resetSectionDefault(int sectionPosition) {
        for (ActionValueClient client : perpetuals.get(sectionPosition))
            client.setDefaults();
    }

    public static void setMenu(Menu menu) {
        applyButton = menu.findItem(R.id.action_apply);
        cancelButton = menu.findItem(R.id.action_cancel);
        refreshButtons();
    }
}
