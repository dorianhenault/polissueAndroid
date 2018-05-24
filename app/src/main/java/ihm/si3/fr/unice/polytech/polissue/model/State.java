package ihm.si3.fr.unice.polytech.polissue.model;

import ihm.si3.fr.unice.polytech.polissue.R;

public enum State {

    NOT_RESOLVED(R.mipmap.ic_cross_custom, R.string.issue_not_resolved),
    RESOLVED(R.mipmap.ic_check_custom, R.string.issue_resolved);

    private int drawableId;
    private int meaning;

    State(int drawableId, int meaning) {
        this.drawableId = drawableId;
        this.meaning = meaning;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getMeaning() {
        return meaning;
    }
}
