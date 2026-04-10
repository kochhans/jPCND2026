package application.models;

public class LiederStueckeComboModel {
    public LiederStueckeComboModel(String stcktitel) {
	super();

	this.stcktitel = stcktitel;
    }

    String stcktitel;

    public String getStcktitel() {
	return stcktitel;
    }

    public void setStcktitel(String stcktitel) {
	this.stcktitel = stcktitel;
    }
    // -------------

    @Override
    public String toString() {
	return stcktitel;
    }

}
