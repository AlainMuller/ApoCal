package fr.alainmuller.android.apocal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: Alain Muller
 * Date: 18/09/12
 * Time: 17:28          ==> installations actives/totales 3 mois plus tard : 1 321 / 2 274 ^_^
 * Activity permettant de tracer un compte à rebours en se basant sur la date de l'apocalypse selon le calendrier Maya
 */
public class CountDownActivity extends Activity {

    private static final String LOG_TAG = "ApoCal - CountDownActivity";
    TextView tvTimer = null;
    MonCompteur counter = null;
    long milli;
    long sec;
    long min;
    long heure;
    long jour;
    // Boutons Aide / Réglages
    ImageButton ibPrefs, ibAide = null;
    // Affichage / Masquage des boutons
    boolean visible = false;
    // On récupère la date de fin depuis les préférences
    SharedPreferences prefs = null;
    int prefsAnnee, prefsMois, prefsJour;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // On récupère les élément du Layout
        tvTimer = (TextView) findViewById(R.id.timer);

        // Chargement de la date de fin du monde depuis les préférences
        chargeDateFin();

        // Bouton Préférences : affichage d'un DatePickerDialog pour MàJ date de fin du monde
        ibPrefs = (ImageButton) findViewById(R.id.ibPrefs);
        ibPrefs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MonDatePickerDialog datePickerDialog = new MonDatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Log.d(LOG_TAG, "Mise à jour de la date : " + datePicker.toString() + "(" + i + "/" + i2 + "/" + i3 + ")");
                        // Persistance de la date saisie dans les préférences
                        prefs.edit().putInt("annee", datePicker.getYear()).commit();
                        prefs.edit().putInt("mois", datePicker.getMonth()).commit();
                        prefs.edit().putInt("jour", datePicker.getDayOfMonth()).commit();
                        // Mise à jour des préférences et du compteur
                        chargeDateFin();
                        startCountdown();
                    }
                }, prefsAnnee, prefsMois, prefsJour);
                datePickerDialog.setPermanentTitle(getString(R.string.pickDate));
                datePickerDialog.show();
                showHideMenu();
            }
        });

        // Bouton Aide
        ibAide = (ImageButton) findViewById(R.id.ibAide);
        ibAide.setOnClickListener(new

                                          OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  HelpDialog helpDialog = new HelpDialog(view.getContext());
                                                  helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                  helpDialog.show();
                                                  showHideMenu();
                                              }
                                          });

        // Gestion du clic sur l'Activity : afficher / masquer les boutons
        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        rlMain.setOnClickListener(new

                                          OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  showHideMenu();
                                              }
                                          });

        // Démarrage du décompteur
        startCountdown();
    }

    /**
     * Chargement des données depuis les préférences ou initialisation à la date du 21/12/2012
     */
    private void chargeDateFin() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefsAnnee = prefs.getInt("annee", 2012);
        prefsMois = prefs.getInt("mois", 11);
        prefsJour = prefs.getInt("jour", 21);
    }

    /**
     * Initialisation et lancement du décompteur
     */
    private void startCountdown() {
        // Arrêt du compteur initial si changement de date
        if (counter != null) {
            counter.cancel();
            counter = null;
        }

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
            tvTimer.setText(getText(R.string.after));
        } else {
            // Calcul du temps restant
            long diff = fin.getTimeInMillis() - maintenant.getTimeInMillis();

            // Mise en place du compteur (on le rafraîchit toutes les secondes)
            counter = new MonCompteur(diff, 1000);
            counter.start();
        }
    }

    /**
     * Méthode permettant d'afficher ou masquer les boutons en fonction du booléen visible
     */
    private void showHideMenu() {
        // On affiche ou masque les boutons en fonction de l'état de visible
        int visibility = visible ? View.INVISIBLE : View.VISIBLE;
        // Mise à jour de la visibilité des boutons
        ibAide.setVisibility(visibility);
        ibPrefs.setVisibility(visibility);
        // Chargement de l'animation de fadein / fadeout
        Animation animation = AnimationUtils.loadAnimation(this, visible ? R.anim.fadeout : R.anim.fadein);
        if (animation != null) {
            // Réinitialisation de l'état de l'animation
            animation.reset();
            // Nettoyage / Annulation de tout animation éventuelle en cours
            ibAide.clearAnimation();
            ibPrefs.clearAnimation();
            ibAide.startAnimation(animation);
            ibPrefs.startAnimation(animation);
        }
        // Alternance de l'état visible / invisible
        visible = !visible;
    }

    /**
     * Classe étendant la classe abstraite CountDownTimer
     */
    public class MonCompteur extends CountDownTimer {

        /**
         * Constructeur classique
         *
         * @param millisInFuture    : durée du compte à rebours
         * @param countDownInterval : fréquence d'actualisation
         */
        public MonCompteur(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Méthode déclenchée quand le timer arrive à terme
         */
        @Override
        public void onFinish() {
            tvTimer.setText(getText(R.string.bye));
        }

        /**
         * Méthode déclenchée à la fréquence spécifiée à la création (countDownInterval)
         *
         * @param millisUntilFinished temps restant en millisecondes
         */
        @Override
        public void onTick(long millisUntilFinished) {
            milli = millisUntilFinished % 1000;
            sec = ((millisUntilFinished - milli) / 1000) % 60;
            min = ((millisUntilFinished - milli - sec) / (1000 * 60) % 60);
            heure = ((millisUntilFinished - milli - sec - min) / (1000 * 60 * 60) % 24);
            jour = ((millisUntilFinished - milli - sec - min - heure) / (1000 * 60 * 60 * 24));
            tvTimer.setText(String.format("%02d", jour) + getText(R.string.jour) + " "
                    + String.format("%02d", heure) + getText(R.string.heure) + " "
                    + String.format("%02d", min) + getText(R.string.minute) + " "
                    + String.format("%02d", sec) + getText(R.string.seconde));
        }
    }

    /**
     * Sous-Classe de DatePickerDialog permettant de conserver le titre sur changement de date
     */
    private class MonDatePickerDialog extends DatePickerDialog {
        // Titre permanent du DatePickerDialog
        private CharSequence title;

        public MonDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        public void setPermanentTitle(CharSequence title) {
            this.title = title;
            setTitle(title);
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            setTitle(title);
        }
    }
}
