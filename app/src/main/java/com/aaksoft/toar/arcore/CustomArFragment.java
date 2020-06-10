/*
This Class is for taking runtime permission dangerous permission which
would otherwise be not possible through using built in ArFragment.
*/

package com.aaksoft.toar.arcore;

/*
 *   Created By Aasharib
 *   on
 *   January 6, 2019
 */

import android.Manifest;
import com.google.ar.sceneform.ux.ArFragment;

/*
    This class extends ArFragment to get additional permissions at application startup
 */

public class CustomArFragment extends ArFragment {
    @Override
    public String[] getAdditionalPermissions() {
        String[] additionalPermissions = new String[2];
        additionalPermissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        additionalPermissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        return additionalPermissions;
    }

}
