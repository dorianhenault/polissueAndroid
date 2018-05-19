package ihm.si3.fr.unice.polytech.polissue.model;

public enum State {

    NOT_RESOLVED(10),
    IN_PROGRESS(50),
    RESOLVED(100);

    int progress;

    State(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
