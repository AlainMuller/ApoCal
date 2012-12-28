package fr.alainmuller.android.apocal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Alain Muller
 * Date: 24/09/12
 * Time: 23:29
 * Activity correspondant au widget
 */
public class WidgetActivity extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        /* Paramétrage du Widget (rafraîchissement) */
        // Utilisation d'un Timer pour gérer le rafraîchissement du widget
        Timer timer = new Timer();
        // Rafraîchissement prévu toutes les minutes
        timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 60000);

        /* Gestion du click sur le Widget */
        // Récupération de l'élément
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        // Création de l'Intent pour afficher l'Activity
        Intent intent = new Intent(context, CountDownActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Mise en place de l'onClickListener sur le Layout du Widget
        views.setOnClickPendingIntent(R.id.llWidget, pendingIntent);
        // Mise à jour du Widget
        appWidgetManager.updateAppWidget(appWidgetIds[0], views);
    }

    /**
     * InnerClass permettant de mettre à jour le Widget avec le temps restant.
     */
    private class MyTime extends TimerTask {
        final RemoteViews remoteViews;
        final AppWidgetManager appWidgetManager;
        final ComponentName thisWidget;
        final Context contexte;

        public MyTime(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            // On récupère le contexte de l'activity pour accéder aux ressources
            this.contexte = context;
            // On récupère le layout du widget
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            thisWidget = new ComponentName(context, WidgetActivity.class);
        }

        @Override
        public void run() {
            // Chargement des données depuis les préférences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexte);
            int prefsAnnee = prefs.getInt("annee", 2012);
            int prefsMois = prefs.getInt("mois", 11);
            int prefsJour = prefs.getInt("jour", 21);

            Calendar fin = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            fin.set(Calendar.YEAR, prefsAnnee);
            fin.set(Calendar.MONTH, prefsMois);
            fin.set(Calendar.DAY_OF_MONTH, prefsJour);
            fin.set(Calendar.HOUR, 0);
            fin.set(Calendar.MINUTE, 0);
            fin.set(Calendar.SECOND, 0);
            fin.set(Calendar.MILLISECOND, 0);
            fin.set(Calendar.AM_PM, Calendar.AM);

            // On récupère la date actuelle
            Calendar maintenant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

            // Petite Prévision : et si on ne mourrait pas le 21 Décembre? ^_^"
            if (fin.before(maintenant)) {
                remoteViews.setTextViewText(R.id.tvWidget, contexte.getString(R.string.after));
            } else {
                // Calcul du temps restant
                long millisUntilFinished = fin.getTimeInMillis() - maintenant.getTimeInMillis();

                long milli = millisUntilFinished % 1000;
                long sec = ((millisUntilFinished - milli) / 1000) % 60;
                long min = ((millisUntilFinished - milli - sec) / (1000 * 60) % 60);
                long heure = ((millisUntilFinished - milli - sec - min) / (1000 * 60 * 60) % 24);
                long jour = ((millisUntilFinished - milli - sec - min - heure) / (1000 * 60 * 60 * 24));
                remoteViews.setTextViewText(R.id.tvWidget, String.format("%02d", jour) + contexte.getString(R.string.jour) + " "
                        + String.format("%02d", heure) + contexte.getString(R.string.heure) + " "
                        + String.format("%02d", min) + contexte.getString(R.string.minute));
            }
            // Mise à jour du widget
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }
}
