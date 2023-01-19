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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccApFaktur;
import net.sra.prime.ultima.db.mapper.MapperAccProposalAp;
import net.sra.prime.ultima.entity.AccApFaktur;
import net.sra.prime.ultima.entity.AccProposalAp;
import net.sra.prime.ultima.entity.AccProposalApDetail;
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
public class ServiceProposalAp {

    @Autowired
    MapperAccProposalAp referensi;

    @Autowired
    MapperAccApFaktur mapperAccApFaktur;
    
   
    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
        return referensi.SelectMax(bulan, tahun);
    }

    
    @Transactional(readOnly = true)
    public List<AccProposalAp> onLoadList(Date awal, Date akhir, Character status, Integer departemen) {
        return referensi.selectAll(awal, akhir, status, departemen);
    }
    
    @Transactional(readOnly = true)
    public AccProposalAp onLoad(String id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<AccProposalApDetail> onLoadDetail(String id) {
        return referensi.selectAllDetail(id);
    }
    
    @Transactional(readOnly = true)
    public List<AccApFaktur> selectAllAccApFaktur(Integer batas) {
        return mapperAccApFaktur.selectAll(batas);
    }

    
    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void ubah(AccProposalAp item, List<AccProposalApDetail> lAccProposalApDetail)  {
        referensi.update(item);
        referensi.deleteDetail(item.getNo_proposal());
        tambahdetail(item, lAccProposalApDetail);  
    }
    
    @Transactional(readOnly = false)
    public void maintenance(AccProposalAp item)  {
        referensi.update(item);
    }
    
    
    @Transactional(readOnly = false)
    public void tambah(AccProposalAp item, List<AccProposalApDetail> lAccProposalApDetail)  {
        referensi.insert(item);
        tambahdetail(item, lAccProposalApDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(AccProposalAp item, List<AccProposalApDetail> selectedAccProposalApDetail)  {
        for (int i = 0; i < selectedAccProposalApDetail.size(); i++) {
            AccProposalApDetail itemDetail= new AccProposalApDetail();
            Integer k = i + 1;
            itemDetail = selectedAccProposalApDetail.get(i);
            itemDetail.setUrut(k);
            itemDetail.setNo_proposal(item.getNo_proposal());
            referensi.insertDetail(itemDetail);

        }

    }
    
    @Transactional(readOnly = false)
    public void updateStatus(AccProposalAp item, List<AccProposalApDetail> lAccProposalApDetail)  {
        /// dibawah ini berfungsi agar payment proposal yang lama tidak berfungsi lagi
        //referensi.updateStatusTOOLD();
            referensi.updateStatus(item);
            for (int i = 0; i < lAccProposalApDetail.size(); i++) {
                AccProposalApDetail itemDetail= new AccProposalApDetail();
                itemDetail=lAccProposalApDetail.get(i);
                referensi.updateStatusDetail(itemDetail);
            }
    }

}
