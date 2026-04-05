package view;

import utils.ItemType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Gestisce i timer dell'animazione "juicy scale-up" per le icone HUD.
 *
 * Ogni volta che il giocatore raccoglie un item, il Controlle0r chiama
 * {@link #triggerPickupAnimation(ItemType)}.
 * Il Drawer interroga {@link #getScaleFactor(ItemType)} ogni frame:
 * scaleFactor > 1.0  →  icona ingrandita (picco immediatamente dopo il trigger)
 * scaleFactor = 1.0  →  icona a dimensione standard
 *
 * L'animazione dura {@link #ANIM_DURATION_MS} ms e si risolve tramite
 * System.currentTimeMillis(), indipendentemente dal framerate.
 */
public class HudItemAnimator {

    /** Durata totale dell'animazione ingrandimento (ms). */
    private static final long  ANIM_DURATION_MS = 300L;

    /** Ingrandimento massimo (1.35 = +35%). */
    private static final float MAX_SCALE        = 1.35f;

    // Mappa: ItemType → timestamp di inizio animazione (-1 se nessuna in corso)
    private final Map<ItemType, Long> triggerTimes = new EnumMap<>(ItemType.class);

    // Singleton leggero (usato solo dalla View)
    private static HudItemAnimator instance;

    private HudItemAnimator() {
        for (ItemType t : ItemType.values()) {
            triggerTimes.put(t, -1L);
        }
    }

    public static HudItemAnimator getInstance() {
        if (instance == null) instance = new HudItemAnimator();
        return instance;
    }

    // ------------------------------------------------------------------
    // API pubblica
    // ------------------------------------------------------------------

    /**
     * Avvia l'animazione di ingrandimento per l'icona dell'item dato.
     * Chiamato dal Controller (tramite IControllerForView) quando il
     * model notifica una raccolta.
     */
    public void triggerPickupAnimation(ItemType type) {
        if (type != null) triggerTimes.put(type, System.currentTimeMillis());
    }

    /**
     * Restituisce il fattore di scala corrente [1.0 … MAX_SCALE] per l'icona.
     *
     * La curva è: sale "immediatamente" a MAX_SCALE, poi scende linearmente
     * fino a 1.0 nell'arco di ANIM_DURATION_MS.
     *
     * @return fattore di scala (1.0 = normale)
     */
    public float getScaleFactor(ItemType type) {
        long start = triggerTimes.getOrDefault(type, -1L);
        if (start < 0) return 1.0f;

        long elapsed = System.currentTimeMillis() - start;
        if (elapsed >= ANIM_DURATION_MS) {
            triggerTimes.put(type, -1L); // animazione conclusa
            return 1.0f;
        }
        // Progresso 0 → 1 nel tempo
        float t = (float) elapsed / ANIM_DURATION_MS;
        // Scala scende da MAX_SCALE a 1.0 in modo lineare
        return MAX_SCALE - (MAX_SCALE - 1.0f) * t;
    }

    /** True se un'animazione è in corso per questo tipo (utile per debug). */
    public boolean isAnimating(ItemType type) {
        long start = triggerTimes.getOrDefault(type, -1L);
        return start >= 0 && (System.currentTimeMillis() - start) < ANIM_DURATION_MS;
    }
}
