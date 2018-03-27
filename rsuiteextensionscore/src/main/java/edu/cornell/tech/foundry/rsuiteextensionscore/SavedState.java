package edu.cornell.tech.foundry.rsuiteextensionscore;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference.BaseSavedState;


class SavedState extends BaseSavedState {
    int value; //this will store the current value from ValueBar

    SavedState(Parcelable superState) {
        super(superState);
    }

    private SavedState(Parcel in) {
        super(in);
        value = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(value);
    }

    public static final Creator<SavedState> CREATOR
            = new Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
}
