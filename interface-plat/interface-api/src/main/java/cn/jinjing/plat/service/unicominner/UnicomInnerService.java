package cn.jinjing.plat.service.unicominner;

import cn.jinjing.plat.api.entity.ReLabel;

public interface UnicomInnerService {

    ReLabel getUnicomInnerLabel(String month, String label, String mdn,String userCode);
}
