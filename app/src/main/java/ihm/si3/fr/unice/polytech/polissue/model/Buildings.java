package ihm.si3.fr.unice.polytech.polissue.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public enum Buildings {
    BUILDING1(new PolygonOptions().add(
                    new LatLng(43.615243, 7.072978),
                    new LatLng(43.615340, 7.073133),
                    new LatLng(43.615711, 7.072697),
                    new LatLng(43.615629, 7.072531))
                    .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                    add("E-107");
                    add("E-108");
                    add("E-109");
    }}),
    BUILDING2(new PolygonOptions().add(
            new LatLng(43.615487, 7.072267),
            new LatLng(43.615089, 7.072725),
            new LatLng(43.615187, 7.072879),
            new LatLng(43.615582, 7.072415))
            .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                add("0-107");
                add("0-108");
                add("0-109");
    }}),
    BUILDING3(new PolygonOptions().add(
            new LatLng(43.616108, 7.071947),
            new LatLng(43.616220, 7.072130),
            new LatLng(43.615738, 7.072691),
            new LatLng(43.615631, 7.072511))
            .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                add("E+130");
                add("E+131");
                add("E+132");
    }}),
    BUILDING4(new PolygonOptions().add(
            new LatLng(43.615568, 7.071939),
            new LatLng(43.615744, 7.072220),
            new LatLng(43.615629, 7.072352),
            new LatLng(43.615458, 7.072064))
            .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                add("0+107");
                add("0+108");
                add("0+109");
    }}),
    BUILDING5(new PolygonOptions().add(
            new LatLng(43.615822, 7.071694),
            new LatLng(43.615669, 7.071860),
            new LatLng(43.615796, 7.072066),
            new LatLng(43.615875, 7.071994),
            new LatLng(43.615888,7.071788))
            .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                add("Amphi-Est");
                add("Amphi-Ouest");
    }}),
    BUILDING6(new PolygonOptions().add(
            new LatLng(43.615846, 7.071521),
            new LatLng(43.615707, 7.071304),
            new LatLng(43.615254, 7.071835),
            new LatLng(43.615384, 7.072062))
            .strokeColor(0xFF205DCF).fillColor(0xFF205DCF).strokeWidth(2),
            new ArrayList<String>() {{
                add("0+308");
                add("0+309");
                add("0+310");
    }});

    private PolygonOptions polygonOptions;
    private ArrayList<String> classRooms;

    Buildings(PolygonOptions polygonOptions, ArrayList<String> classRooms){
        this.polygonOptions=polygonOptions;
        this.classRooms=classRooms;
    }

    public PolygonOptions getPolygonOptions(){
        return this.polygonOptions;
    }

    public ArrayList<String> getClassRooms(){
        return this.classRooms;
    }
}
