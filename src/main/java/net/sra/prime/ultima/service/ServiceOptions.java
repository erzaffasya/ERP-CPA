/*
 * Copyright 2017 JoinFaces.
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
package net.sra.prime.ultima.service;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperHargaBeli;
import net.sra.prime.ultima.db.mapper.MapperHrDepartemen;
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.Agama;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.Family;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HrDepartemen;
import net.sra.prime.ultima.entity.HrPegawai;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.KategoriBarang;
import net.sra.prime.ultima.entity.KategoriCustomer;
import net.sra.prime.ultima.entity.KategoriSupplier;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.MataUang;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.SatuanBesar;
import net.sra.prime.ultima.entity.SatuanKecil;
import net.sra.prime.ultima.entity.Sektor;
import net.sra.prime.ultima.entity.SupplierKontak;
import net.sra.prime.ultima.entity.finance.LoanType;
import net.sra.prime.ultima.entity.hr.HubunganKeluarga;
import net.sra.prime.ultima.entity.hr.Kabupaten;
import net.sra.prime.ultima.entity.hr.Provinsi;
import net.sra.prime.ultima.entity.hr.StatusAbsen;
import net.sra.prime.ultima.entity.kpi.Category;
import net.sra.prime.ultima.entity.kpi.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceOptions {
    
    
    @Autowired
    MapperReferensi referensi;
    
    @Autowired
    MapperCustomer mapperCustomer;
    
    @Autowired
    MapperHargaBeli mapperHargaBeli;
    
    @Autowired
    MapperSupplier mapperSupplier;
    
    @Autowired
    MapperAccount mapperAccount;
    
    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperHrJabatan mapperHrJabatan;
    
    @Autowired
    MapperHrDepartemen mapperHrDepartemen;
    
    @Autowired
    MapperKantor mapperKantor;
    
    
    @Transactional(readOnly = true)
    public List<KategoriCustomer> selectKategoriCustomers(){
        return referensi.selectKategoriCustomer();
    }

    @Transactional(readOnly = true)
    public List<Sektor> selectSektor(){
        return referensi.selectSektor();
    }
    
    @Transactional(readOnly = true)
    public List<KategoriSupplier> selectKategoriSupplier(){
        return referensi.selectKategoriSupplier();
    }

    @Transactional(readOnly = true)
    public List<KategoriBarang> selectKategoriBarang(){
        return referensi.selectKategoriBarang();
    }
    
    @Transactional(readOnly = true)
    public List<SatuanBesar> selectSatuanBesar(){
        return referensi.selectSatuanBesar();
    }
    
    @Transactional(readOnly = true)
    public List<SatuanKecil> selectSatuanKecil(){
        return referensi.selectSatuanKecil();
    }
    
    @Transactional(readOnly = true)
    public List<MataUang> selectMataUang(){
        return referensi.selectMataUang();
    }
    
    @Transactional(readOnly = true)
    public List<Customer> selectComboCustomer(){
        return referensi.selectComboCustomer();
    }
    
    @Transactional(readOnly = true)
    public List<Gudang> selectComboGudangAll() {
        return referensi.selectComboGudangAll();
    }
    
    @Transactional(readOnly = true)
    public List<Gudang> selectComboGudang(String idpegawai){
        return referensi.selectComboGudang(idpegawai);
    }
    
    @Transactional(readOnly = true)
    public List<Gudang> selectComboGudangAllWithParent() {
        return referensi.selectComboGudangAllWithParent();
    }
    
    @Transactional(readOnly = true)
    public List<Barang> selectComboBarang() {
        return referensi.selectComboBarang();
    }
    
    @Transactional(readOnly = true)
    public List<HrPegawai> selectComboPegawai() {
        return referensi.selectComboPegawai();
    }
    
    @Transactional(readOnly = true)
    public List<CustomerPengiriman> selectAllpengiriman(String id_customer) {
        return mapperCustomer.selectAllpengiriman(id_customer);
    }
    
    @Transactional(readOnly = true)
    public List<HargaBeli> selectBuyContract(String id_supplier, String id_customer) {
        return mapperHargaBeli.selectBuyContract(id_supplier, id_customer);
    }
    
    
    @Transactional(readOnly = true)
    public List<SupplierKontak> selectAllKontakSupplier(String id_supplier){
        return mapperSupplier.selectAllKontak(id_supplier);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerKontak> selectAllKontakCustomer(String id_customer){
        return mapperCustomer.selectAllKontak(id_customer);
    }
    
    
    @Transactional(readOnly = true)
    public List<Account> selectLevel(Integer level){
        return mapperAccount.selectLevel(level);
    }
    
    @Transactional(readOnly = true)
    public List<InternalKantorCabang> selectComboKantor(){
        return referensi.selectComboKantor();
    }
    
    @Transactional(readOnly = true)
    public List<Family> selectComboFamily(){
        return referensi.selectComboFamily();
    }
    
    @Transactional(readOnly = true)
    public List<Account> selectComboKas(){
        return referensi.selectComboKas();
    }
    
    
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketing(Integer id_departemen,String id_kantor){
        return mapperPegawai.selectComboMarketing(id_departemen,id_kantor);
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketingAll(){
        return mapperPegawai.selectComboMarketingAll();
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectComboDriver(){
        return mapperPegawai.selectComboDriver();
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketingAja(){
        return mapperPegawai.selectComboMarketingAja();
    }
    
    @Transactional(readOnly = true)
    public List<Agama> listAgama(){
        return referensi.selectAgama();
    }
    
    @Transactional(readOnly = true)
    public List<Provinsi> listProvinsi(){
        return mapperPegawai.selecProvinsi();
    }
    
    @Transactional(readOnly = true)
    public List<Kabupaten> listKabupaten(String idProvinsi){
        return mapperPegawai.selecKabupaten(idProvinsi);
    }
    
    @Transactional(readOnly = true)
    public List<HubunganKeluarga> listHubunganKeluarga(){
        return referensi.selectComboHubunganKeluarga();
    }
    
    @Transactional(readOnly = true)
    public List<StatusAbsen> selectAllStatusAbsens(){
        return referensi.selectAllStatusAbsen();
    }
    
    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadListJabatan() {
        return mapperHrJabatan.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<HrDepartemen> onLoadListDepartemen() {
        return mapperHrDepartemen.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<InternalKantorCabang> onLoadListKantor() {
        return mapperKantor.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<LoanType> onLoanListLoanType() {
        return referensi.selectAllLoanType();
    }
    
    @Transactional(readOnly = true)
    public List<Category> onLoadKpiCategori() {
        return referensi.selectAllKpiCategory();
    }
    
    @Transactional(readOnly = true)
    public List<Unit> onLoadKpiUnit() {
        return referensi.selectAllKpiUnit();
    }
}
