package net.sra.prime.ultima.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author Hairian
 */
@Mapper
@Getter
@Setter

public class StokOpnameDetil implements java.io.Serializable {

    private Integer id;
    private Double qty;
    private Double qty_stok;
    private Double selisih;
    private Double total;
    private String id_gudang;
    private String id_barang;
    private String barang;
    private String notes;
    private Boolean isreason;
    private Double isi_satuan;
    private String satuan_besar;
    private String satuan_kecil;
}
