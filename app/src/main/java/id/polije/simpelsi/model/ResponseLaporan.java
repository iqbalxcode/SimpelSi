package id.polije.simpelsi.model;

import java.util.List;
import id.polije.simpelsi.CekStatusLaporan.Laporan;

public class ResponseLaporan {
    private String status;
    private List<Laporan> data;

    public String getStatus() { return status; }
    public List<Laporan> getData() { return data; }
}