package cn.jinjing.plat.service.user;

import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.api.entity.ReObject;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {

    /**
     * 登录
     * @param userCode
     * @param passwd
     * @return
     */
    public ReObject logon(String userCode, String passwd);

    /**
     * 记录用户调用接口日志
     * @param interfaceLog required
     * @return
     */
    public ReObject insertLog(InterfaceLog interfaceLog);

    /**
     * 获取用户缓存信息
     * @param  userCode required
     * @return CacheUserLabel
     */
    public CacheUserLabel getUserLabelInfo(String userCode, String labelCode, String type, String telcom);

    /**
     * 获取所有用户
     * @return List<String>
     */
    public List<String> getAllUserCode();

}
