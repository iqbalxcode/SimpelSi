package id.polije.simpelsi.model;

import java.util.List;

public class ResponseLaporan {
    private String status;
    private List<LaporanModel> data;

    public String getStatus() { return status; }
    public List<LaporanModel> getData() { return data; }
}
