package cn.itrip.auth;

import cn.itrip.biz.SMS;
import cn.itrip.biz.TokenBiz;
import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ItripTokenVO;
import cn.itrip.common.MD5;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripUser;
import cn.itrip.pojo.ItripUserVO;
import cn.itrip.util.RedisHelp;
import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.regexp.internal.REUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Random;

@Api(value = "user",description = "用户的模块")
@Controller
@RequestMapping("api")
public class UserController {

    @Resource
    ItripUserMapper dao;


    @Resource
    RedisHelp help;

    @Resource
    TokenBiz tokenBiz;

    @Resource
    SMS sms;

    //手机注册
    @RequestMapping(value = "/registerbyphone", method = RequestMethod.POST, produces = "application/json; charset=utf-8")

    public @ResponseBody Dto Resgiter(@RequestBody ItripUserVO userVO) throws Exception {
        try {
            //把数据插入到数据库中
            ItripUser itripUser = new ItripUser();
            itripUser.setActivated(0);
            itripUser.setUserCode(userVO.getUserCode());
            itripUser.setUserName(userVO.getUserName());
            itripUser.setUserPassword(MD5.getMd5(userVO.getUserPassword(), 32));
            dao.insertItripUser(itripUser);
            //为手机发送验证码并存入redis中
            Random random = new Random(4);
            int number = random.nextInt(9999);
            sms.SentSms(userVO.getUserCode(), number + "");
            help.SetData("Code:" + userVO.getUserCode(), number + "");
            return DtoUtil.returnSuccess();

        } catch (Exception ex) {
            return DtoUtil.returnFail("注册失败","1000");
        }
    }



        @RequestMapping(value="/json",method=RequestMethod.GET,produces="application/json; charset=utf-8")
        @ResponseBody
        public Object Getlist() throws Exception {
            ItripUser user=dao.getItripUserById(new Long(12));
            System.out.println(user.getUserName());
            help.SetData("wang","zhaoze");
            return JSONArray.toJSONString(user);
        }


    @RequestMapping(value="/validatephone",method=RequestMethod.PUT,produces="application/json; charset=utf-8")

    public @ResponseBody Dto validatephone(@RequestParam String user, @RequestParam String code){
        try{
            //验证redis中是否有
            String oldstr=help.GetData(user);
            if(oldstr!=null&&oldstr.equals(code)) {
                //如果有修改数据库数据
                dao.updateStatus(user);
            }
            return DtoUtil.returnSuccess("激活成功");
        }catch(Exception ex){
           return  DtoUtil.returnFail("激活失败","10000");
        }


    }



    @ApiImplicitParams({
            @ApiImplicitParam(paramType="form",required=true,value="用户名",name="name",defaultValue="itrip@163.com"),
            @ApiImplicitParam(paramType="form",required=true,value="密码",name="password",defaultValue="111111"),
    })

    @RequestMapping(value="/dologin",method=RequestMethod.POST,produces="application/json; charset=utf-8")
    @ResponseBody
    public Dto login(String name,String password,HttpServletRequest request) throws Exception {
        ItripUser user = dao.login(name, MD5.getMd5(password, 32));
        if (user != null)
        {
            //把user存入redis中
            // 生成一个token
            String token=tokenBiz.generateToken(request.getHeader("User-Agent"),user);

            help.SetData(token,JSONArray.toJSONString(user));

            ItripTokenVO tokenVO=new ItripTokenVO(token,Calendar.getInstance().getTimeInMillis()*3600*2,Calendar.getInstance().getTimeInMillis());
          return DtoUtil.returnDataSuccess(tokenVO);
           // return  DtoUtil.returnFail("用户登录失败","1000");
        }

      return  DtoUtil.returnFail("用户登录失败","1000");
    }
/*
   /* @RequestMapping(value="/dologin",method=RequestMethod.GET)
    @ResponseBody
    public Object Login(HttpServletRequest request, String name, String password) throws Exception {
*/
     /*   ItripUser user = dao.login(name, MD5.getMd5(password, 32));
        if (user != null)
        {
            //把user存入redis 中
            // 生成一个token
             String token=tokenBiz.generateToken(request.getHeader("User-Agent"),user);

             help.SetData(token,JSONArray.toJSONString(user));

            ItripTokenVO tokenVO=new ItripTokenVO(token,Calendar.getInstance().getTimeInMillis()*3600*2,Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnDataSuccess(tokenVO);
        }*/
     /*   return  DtoUtil.returnFail("用户登录失败","1000");

    }
*/
}
