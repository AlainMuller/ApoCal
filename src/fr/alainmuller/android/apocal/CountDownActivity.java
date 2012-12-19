package fr.alainmuller.android.apocal;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    RelativeLayout rlMain = null;
    // Affichage / Masquage des boutons
    boolean visible = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // On récupère les élément du Layout
        tvTimer = (TextView) findViewById(R.id.timer);

        // On récupère la date de la fin du monde (21/12/2012)
        Calendar fin = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        fin.set(Calendar.YEAR, 2012);
        fin.set(Calendar.MONTH, Calendar.DECEMBER);
        fin.set(Calendar.DAY_OF_MONTH, 21);
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

        // Click sur Préférences
        ibPrefs = (ImageButton) findViewById(R.id.ibPrefs);
        ibPrefs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(CountDownActivity.this, "Click sur bouton préférences!", Toast.LENGTH_SHORT).show();
            }
        });

        // Click sur Aide
        ibAide = (ImageButton) findViewById(R.id.ibAide);
        ibAide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpDialog helpDialog = new HelpDialog(view.getContext());
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.show();
            }
        });

        // Gestion du click sur l'Activity : afficher / masquer les boutons
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        rlMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideMenu();
            }
        });
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
}
