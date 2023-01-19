/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sra.prime.ultima.admin;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Gudang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import net.sra.prime.ultima.entity.KategoriCustomer;
import net.sra.prime.ultima.entity.KategoriSupplier;
import net.sra.prime.ultima.entity.KategoriBarang;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.Agama;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.Family;
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HrDepartemen;
import net.sra.prime.ultima.entity.SatuanKecil;
import net.sra.prime.ultima.entity.MataUang;
import net.sra.prime.ultima.entity.SatuanBesar;
import net.sra.prime.ultima.entity.HrPegawai;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Sektor;
import net.sra.prime.ultima.entity.SupplierKontak;
import net.sra.prime.ultima.entity.finance.LoanType;
import net.sra.prime.ultima.entity.hr.HubunganKeluarga;
import net.sra.prime.ultima.entity.hr.Kabupaten;
import net.sra.prime.ultima.entity.hr.Provinsi;
import net.sra.prime.ultima.entity.hr.StatusAbsen;
import net.sra.prime.ultima.entity.kpi.Category;
import net.sra.prime.ultima.entity.kpi.Unit;
import net.sra.prime.ultima.service.ServiceOptions;

/**
 *
 * @author Hairian
 */
@Named("options")
//@ConversationScoped
@ViewScoped
@Getter
@Setter
public class Options implements Serializable {

    private static final long serialVersionUID = -94660532783957684L;

    @Autowired
    ServiceOptions serviceOptions;
    
    private String idpegawai;
    
    //private MapperReferensi mapper;
    @PostConstruct
    public void awal() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getAutowireCapableBeanFactory().autowireBean(this);
        //mapper = sqlSessionFactory.openSession().getMapper(MapperReferensi.class);

    }

    public Map<String, String> getKategoriCustomer() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<KategoriCustomer> list = serviceOptions.selectKategoriCustomers();
            if (idkategoricustomer == null || "".equals(idkategoricustomer)) {
                idkategoricustomer = Integer.toString(list.get(0).getId_kategori_customer());
            }
            map.put("Pilih Kategori Customer", "");
            list.stream().forEach((data) -> {
                map.put(data.getNm_kategori_customer(), Integer.toString(data.getId_kategori_customer()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    private String idkategoricustomer;

    public Map<String, String> getSektor() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Sektor> list = serviceOptions.selectSektor();
            if (idsektor == null || "".equals(idsektor)) {
                idsektor = Integer.toString(list.get(0).getId_sektor());
            }
            map.put("Pilih Sektor", "");
            list.stream().forEach((data) -> {
                map.put(data.getSektor(), Integer.toString(data.getId_sektor()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idsektor;

    public Map<String, String> getKategoriSupplier() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<KategoriSupplier> list = serviceOptions.selectKategoriSupplier();
            if (idkategorisupplier == null || "".equals(idkategorisupplier)) {
                idkategorisupplier = Integer.toString(list.get(0).getId_kategori_supplier());
            }
            map.put("Pilih Vendor", "");
            list.stream().forEach((data) -> {
                map.put(data.getNm_kategori_supplier(), Integer.toString(data.getId_kategori_supplier()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idkategorisupplier;

    public Map<String, String> getKategoriBarang() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<KategoriBarang> list = serviceOptions.selectKategoriBarang();
            if (idkategoribarang == null || "".equals(idkategoribarang)) {
                idkategoribarang = list.get(0).getId_kategori_barang();
            }
            map.put("Pilih Kategori Barang", "");
            list.stream().forEach((data) -> {
                map.put(data.getKategori_barang(), data.getId_kategori_barang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idkategoribarang;

    public Map<String, String> getSatuanBesar() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<SatuanBesar> list = serviceOptions.selectSatuanBesar();
            map.put("Pilih Satuan", "");
            list.stream().forEach((data) -> {
                map.put(data.getSatuan_besar(), data.getId_satuan_besar());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idsatuanbesar;

    public Map<String, String> getSatuan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<SatuanKecil> list = serviceOptions.selectSatuanKecil();
            if (idsatuankecil == null || "".equals(idsatuankecil)) {
                idsatuankecil = list.get(0).getId_satuan_kecil();
            }
            list.stream().forEach((data) -> {
                map.put(data.getSatuan_kecil(), data.getId_satuan_kecil());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idsatuankecil;

    public Map<String, String> getMataUang() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MataUang> list = serviceOptions.selectMataUang();
            if (kodematauang == null || "".equals(kodematauang)) {
                kodematauang = list.get(0).getKode_mata_uang();
            }
            list.stream().forEach((data) -> {
                map.put(data.getMata_uang(), data.getKode_mata_uang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    private String kodematauang;

    public Map<String, String> getComboCustomer() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Customer> list = serviceOptions.selectComboCustomer();
            if (idkontak == null || "".equals(idkontak)) {
                idkontak = list.get(0).getId_kontak();
            }
            list.stream().forEach((data) -> {
                map.put(data.getCustomer(), data.getId_kontak());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idkontak;

    public Map<String, String> getComboGudang() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<Gudang> list = new ArrayList<>();
        try {
            list = serviceOptions.selectComboGudangAll();

            list.stream().forEach((data) -> {
                map.put(data.getGudang(), data.getId_gudang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboGudangByPegawai() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<Gudang> list = new ArrayList<>();
        try {
            list = serviceOptions.selectComboGudang(idpegawai);
            list.stream().forEach((data) -> {
                map.put(data.getGudang(), data.getId_gudang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboGudangAll() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Gudang> list = serviceOptions.selectComboGudangAllWithParent();

            list.stream().forEach((data) -> {
                map.put(data.getGudang(), data.getId_gudang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idgudang;

    public Map<String, String> getComboBarang() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Barang> list = serviceOptions.selectComboBarang();
            if (idbarang == null || "".equals(idbarang)) {
                idbarang = list.get(0).getId_barang();
            }
            list.stream().forEach((data) -> {
                map.put(data.getNama_barang(), data.getId_barang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idbarang;

    public Map<String, String> getPegawai() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<HrPegawai> list = serviceOptions.selectComboPegawai();
            if (idpegawai == null || "".equals(idpegawai)) {
                idpegawai = Integer.toString(list.get(0).getId_pegawai());
            }
            list.stream().forEach((data) -> {
                map.put(data.getNama_depan(), Integer.toString(data.getId_pegawai()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    

    public Map<String, String> getComboCustomerPengiriman() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<CustomerPengiriman> list = serviceOptions.selectAllpengiriman(id_customer);
            map.put("Pilih Alamat Pengiriman", "");
            if (!list.isEmpty()) {
                if (idpengiriman == null || "".equals(idpengiriman)) {
                    idpengiriman = Integer.toString(list.get(0).getId_pengiriman());
                }
                list.stream().forEach((data) -> {
                    map.put(data.getAlamat(), Integer.toString(data.getId_pengiriman()));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idpengiriman;
    private String id_customer;

    public Map<String, String> getComboKontrakPembeliaan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (id_supplier != null) {
            try {
                List<HargaBeli> list = serviceOptions.selectBuyContract(id_supplier, id_customer);
                map.put("Pilih Buy Contract", "");
                if (!list.isEmpty()) {
                    list.stream().forEach((data) -> {
                        map.put(data.getNama_kontrak(), data.getNo_kontrak());
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    private String id_supplier;
    private String nokontrak;

    public Map<String, String> getComboKontakSupplier() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (id_supplier != null) {
            try {
                List<SupplierKontak> list = serviceOptions.selectAllKontakSupplier(id_supplier);
                map.put("Pilih Kontak Person", "");
                if (!list.isEmpty()) {
                    list.stream().forEach((data) -> {
                        map.put(data.getKontak(), Integer.toString(data.getUrut()));
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    public Map<String, String> getComboKontakCustomer() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (id_customer != null) {
            try {
                List<CustomerKontak> list = serviceOptions.selectAllKontakCustomer(id_customer);
                map.put("Pilih Kontak Person", "");
                if (!list.isEmpty()) {
                    list.stream().forEach((data) -> {
                        map.put(data.getKontak(), Integer.toString(data.getId_kontak()));
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    public Map<String, String> getComboLevel() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Level", "");
            for (int i = 1; i <= 4; i++) {
                map.put(Integer.toString(i), Integer.toString(i));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private Integer id_kantor_cabang;
    private Integer level;

    public Map<String, String> getComboParentAccount() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (level != null && level != 0) {
            try {
                List<Account> list = serviceOptions.selectLevel(level);
                map.put("Pilih Parent", "");
                if (!list.isEmpty()) {
                    list.stream().forEach((data) -> {
                        map.put(data.getAccount() + " (" + Integer.toString(data.getId_account()) + ")", Integer.toString(data.getId_account()));
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    private String id_perusahaan;

    public Map<String, String> getComboKantor() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            List<InternalKantorCabang> list = serviceOptions.selectComboKantor();
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_kantor_cabang());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboFamily() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            List<Family> list = serviceOptions.selectComboFamily();
            map.put("Pilih Family", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getFamily(), data.getId_family());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboKas() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Account> list = serviceOptions.selectComboKas();
            map.put("Pilih Kas / Bank", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getAccount(), Integer.toString(data.getId_account()));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getRoles() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            List<Account> list = serviceOptions.selectComboKas();
            map.put("Pilih Roles", "");
            map.put("Operator", "ROLE_OPERATOR");
            map.put("Supervisor", "ROLE_SUPERVISOR");
            map.put("Admin", "ROLE_ADMIN");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getTriwulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("Pilih Triwulan", "");
            map.put("Triwulan I", "1");
            map.put("Triwulan II", "2");
            map.put("Triwulan III", "3");
            map.put("Triwulan IV", "4");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    private Integer id_departemen;
    private String id_kantor;

    public Map<String, String> getComboMarketing() {
        List<Pegawai> list = serviceOptions.selectComboMarketing(id_departemen, id_kantor);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Salesman / Marketing", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboMarketingAll() {
        List<Pegawai> list = serviceOptions.selectComboMarketingAll();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Salesman / Marketing", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public Map<String, String> getComboDriver() {
        List<Pegawai> list = serviceOptions.selectComboDriver();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboMarketingAja() {
        List<Pegawai> list = serviceOptions.selectComboMarketingAja();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Salesman / Marketing", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getSupplierOsPo() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("Pilih Semua", "");
            map.put("Shell", "1");
            map.put("Non Shell", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getJenisKelamin() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("--", "");
            map.put("Laki-laki", "l");
            map.put("Perempuan", "p");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getStatusPernikahan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("Belum kawin", "1");
            map.put("Kawin", "2");
            map.put("Cerai hidup", "3");
            map.put("Cerai mati", "4");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getComboAgama() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Agama> list = serviceOptions.listAgama();

            map.put("---", "");
            list.stream().forEach((data) -> {
                map.put(data.getAgama(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboGolonganDarah() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("--", "");
            map.put("O", "O");
            map.put("A", "A");
            map.put("B", "B");
            map.put("AB", "AB");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getProvinsi() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Provinsi> list = serviceOptions.listProvinsi();

            map.put("Pilih Provinsi", "");
            list.stream().forEach((data) -> {
                map.put(data.getProvinsi(), data.getId());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idProvinsi;

    public Map<String, String> getKabupaten() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Kabupaten> list = serviceOptions.listKabupaten(idProvinsi);

            map.put("Pilih Kabupaten", "");
            list.stream().forEach((data) -> {
                map.put(data.getKabupaten(), data.getId());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String idProvinsiDomisili;

    public Map<String, String> getKabupatenDomisili() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Kabupaten> list = serviceOptions.listKabupaten(idProvinsiDomisili);

            map.put("Pilih Kabupaten", "");
            list.stream().forEach((data) -> {
                map.put(data.getKabupaten(), data.getId());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboHubunganKeluarga() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<HubunganKeluarga> list = serviceOptions.listHubunganKeluarga();

            map.put("Pilih Hubungan Keluarga", "");
            list.stream().forEach((data) -> {
                map.put(data.getHubungan(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboBulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Bulan", "");
            map.put("Januari", "1");
            map.put("Februari", "2");
            map.put("Maret", "3");
            map.put("April", "4");
            map.put("Mei", "5");
            map.put("Juni", "6");
            map.put("Juli", "7");
            map.put("Agustus", "8");
            map.put("September", "9");
            map.put("Oktober", "10");
            map.put("November", "11");
            map.put("Desember", "12");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getComboYears() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("2019", "2019");
            map.put("2020", "2020");
            map.put("2021", "2021");
            map.put("2022", "2022");
            map.put("2023", "2023");
            map.put("2024", "2024");
            map.put("2025", "2025");
            map.put("2026", "2026");
            map.put("2027", "2027");
            map.put("2028", "2028");
            map.put("2029", "2029");
            map.put("2030", "2030");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getComboStatusKaryawan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Permanent", "1");
            map.put("Kontrak", "2");
            map.put("Probation", "3");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getComboStatusAbsen() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<StatusAbsen> list = serviceOptions.selectAllStatusAbsens();
            list.stream().forEach((data) -> {
                map.put(data.getStatus_absen(), data.getId_status_absen());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public String getNamaBulan(Integer bln) {
        switch (bln) {
            case 1:
                return "Januari";
            case 2:
                return "Februari";
            case 3:
                return "Maret";
            case 4:
                return "April";
            case 5:
                return "Mei";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "Agustus";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "Invalid month";
        }
    }

    public String getNamaBulanbyDate(Date tanggal) {
        DateFormat tgl = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        DateFormat tgls = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        return getNamaBulan(Integer.parseInt(tgls.format(tanggal))) + " " + tgl.format(tanggal);
    }

    public Map<String, String> getComboDepartement() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<HrDepartemen> list1 = serviceOptions.onLoadListDepartemen();
            list1.stream().forEach((data) -> {
                map.put(data.getDepartemen(), Integer.toString(data.getId_departemen()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboJabatan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MasterJabatan> list = serviceOptions.onLoadListJabatan();
            list.stream().forEach((data) -> {
                map.put(data.getJabatan(), Integer.toString(data.getId_jabatan()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public Map<String, String> getOptionLoanType() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<LoanType> list = serviceOptions.onLoanListLoanType();
            list.stream().forEach((data) -> {
                map.put(data.getLoan_type(), Integer.toString(data.getId_loan_type()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public Map<String, String> getOptionKpiCategory() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Category> list = serviceOptions.onLoadKpiCategori();
            list.stream().forEach((data) -> {
                map.put(data.getCategory(), Integer.toString(data.getId_category()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public Map<String, String> getOptionKpiUnit() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Unit> list = serviceOptions.onLoadKpiUnit();
            list.stream().forEach((data) -> {
                map.put(data.getUnit(), Integer.toString(data.getId_unit()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
