package cn.jinjing.plat.service.postal;

import cn.jinjing.plat.api.entity.ReObject;

import java.util.List;

public interface PostalService {

    public ReObject getPostalLabel(List<String> address, String label, String divLimit);

}
