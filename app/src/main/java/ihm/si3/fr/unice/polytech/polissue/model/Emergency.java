package ihm.si3.fr.unice.polytech.polissue.model;


import ihm.si3.fr.unice.polytech.polissue.R;

public enum Emergency{

    HIGH(R.drawable.emergency_light_red, R.string.high_emergency),
    MEDIUM(R.drawable.emergency_light_orange, R.string.medium_emergency),
    LOW(R.drawable.emergency_light_green, R.string.low_emergency);

    private int drawableID;
    private int meaning;


    Emergency(int drawableID, int meaning) {
        this.drawableID = drawableID;
        this.meaning = meaning;
    }

    public int getMeaning() {
        return meaning;
    }

    public int getDrawableID() {
        return drawableID;
    }
}
