package fr.alainmuller.android.apocal;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Alain Muller
 * Date: 18/12/12
 * Time: 17:28
 * Dialog permettant d'afficher l'aide de l'application
 */
class HelpDialog extends Dialog {
    private static final String LOG_TAG = "ApoCal - HelpDialog";
    private static Context mContext = null;

    public HelpDialog(Context context) {
        super(context);
        mContext = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chargement du layout
        setContentView(R.layout.help);

        // Chargement des infos de l'application
        TextView tvHelpInfos = (TextView) findViewById(R.id.tvHelpInfos);
        tvHelpInfos.setText(getApplicationName() + " v." + getVersionNumber());

        // Chargement de l'aide (page HTML)
        WebView wvHelpText = (WebView) findViewById(R.id.wvHelpText);
        wvHelpText.loadUrl(mContext.getString(R.string.helpFile));
    }

    /**
     * Retourne la version de l'application
     */
    private String getVersionNumber() {
        String version = "?";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Package non trouvé", e);
        }
        return version;
    }

    /**
     * Retourne le nom de l'application
     */
    private String getApplicationName() {
        String name = "?";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            name = mContext.getString(pi.applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Package non trouvé", e);
        }
        return name;
    }
}
