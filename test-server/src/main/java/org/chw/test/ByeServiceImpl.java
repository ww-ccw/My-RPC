package org.chw.test;

import org.chw.rpc.annotation.Service;
import org.chw.rpc.api.ByeService;

/**
 * @Author CHW
 * @Date 2023/4/23
 **/
@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
