package com.yc.xiaomi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yc.bean.*;
import com.yc.xiaomi.dao.*;
import com.yc.xiaomi.model.BloomFilterConstant;
import com.yc.xiaomi.util.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService,UserService {
    @Autowired
    private UserDao userdao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private VoucherDAO voucherDAO;
    @Autowired
    private VoucherItemInfoDAO voucherItemInfoDAO;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper mapper;
    //redis缓存封装类  TODO:还未完全完善好,所以暂未使用
    @Autowired
    private CacheService cacheService;
    //注入指定的布隆过滤器
    @Resource
    @Qualifier(BloomFilterConstant.NAME_SECKILL)
    private RBloomFilter<Object> bloomFilter;
    //redisson客户端
    @Autowired
    private RedissonClient redissonClient;

    //初始化布隆过滤器,将数据填入布隆过滤器中(在项目启动时注入)
    @PostConstruct
    public void initBloomFilter(){
        //将商品id填入布隆过滤器中
        List<String> list=  userdao.selectAllId();
        for (String data: list){
            bloomFilter.add(data);
        }
    }

    //初始化优惠券
    @PostConstruct
    public void initVoucher(){
        //获取所有优惠券的信息
        List<Voucher> list=  voucherDAO.selectAll();
        //获取已经获取优惠券的用户id
        List<Integer> list2=voucherItemInfoDAO.selectUid();
        for (Voucher data: list){
            //将优惠券的库存初始化进redis中
            stringRedisTemplate.opsForValue().set("voucher:"+data.getId(),String.valueOf(data.getStock()));
            //设置随机过期时间
            double RandomTime = Math.random() * 5 + 1;
            stringRedisTemplate.expire("voucher:"+data.getId(), Duration.ofHours((long) RandomTime ));
        }
        for (Integer id:list2){
            //初始化已经获取优惠券的用户id进redis中
            stringRedisTemplate.opsForValue().setIfAbsent("voucherUid:"+id,String.valueOf(id));
            //设置随机过期时间
            double RandomTime = Math.random() * 5 + 1;
            stringRedisTemplate.expire("voucherUid:"+id, Duration.ofHours((long) RandomTime ));

        }
    }

    //秒杀优惠券
    //TODO:应加入写入mysql后更新缓存的内容,但因为时间关系还未实现
    @Override
    public String KillVoucher(Integer id, Integer vid) {
        //判断优惠券是否初始化成功
        if(Integer.parseInt(stringRedisTemplate.opsForValue().get("voucher:"+vid))<=0){
            return "秒杀还未开始";
        }
        //尝试获取锁
        RLock rLock=redissonClient.getLock("voucher:"+vid);
        try {
            //尝试加锁最多等待10秒,等待10秒后自动释放,时间单位为秒
            boolean locked=rLock.tryLock(10,10, TimeUnit.SECONDS);
            if (locked){
                //加锁成功
                //从redis中获取优惠券的库存
                int stock=Integer.parseInt(stringRedisTemplate.opsForValue().get("voucher:"+vid));
                //通过集合获取voucherUid下面的所有key的值
                Set<String> keys=stringRedisTemplate.keys("voucherUid:*");
                List<String> userSet=stringRedisTemplate.opsForValue().multiGet(keys);
                //判断用户是否已经参与秒杀
                if (userSet!=null && userSet.contains(""+id)){
                    return "您已经秒杀过,请勿重复秒杀";
                }
                //库存不足,秒杀结束
                if (stock<=0){
                    return "很遗憾,秒杀已经结束";
                }
                //加入到秒杀集合
                stringRedisTemplate.opsForValue().set("voucherUid:"+id,""+id);
                //设置随机过期时间
                double RandomTime = Math.random() * 5 + 1;
                stringRedisTemplate.expire("voucherUid:"+id,Duration.ofHours((long) RandomTime ));
                //库存减1
                stringRedisTemplate.opsForValue().decrement("voucher:"+vid);
                return "恭喜您,秒杀成功";
            }
        } catch (InterruptedException e) {
            log.error("异常:{}",e.getMessage());
        }finally {
            //释放锁
            rLock.unlock();
        }
        return "服务器繁忙";
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userdao.selectUserByTelOREmail(username);
        if (user!=null){
            //spring security的User类                                权限
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPwd(),new ArrayList<>());
        }else {
            throw new UsernameNotFoundException("User not found with username:"+username);
        }
    }

    public UserDetails loadUserByUsername2(String username) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("username",username);
        User user=userdao.selectOne(wrapper);
        if (user!=null){
            //spring security的User类                                权限
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPwd(),new ArrayList<>());
        }else {
            throw new UsernameNotFoundException("User not found with username:"+username);
        }
    }

    //根据aid删除
    @Override
    public int deleteAddress(Integer id) {
        int result=addressDao.deleteById(id);
        return result;
    }

    //根据商品名或订单号查询
    @Override
    public List<Map<String, Object>> selectByInoOrName(Integer id, String message, int start, int pagesize) {
        List<Map<String,Object>> list=  orderDao.selectByInoOrName(id,message,start,pagesize);
        return list;
    }

    //查询选定编号的订单
    @Override
    public Map<String, Object> selectOnlyOrder(Integer id, Integer ino) {
        Map<String,Object> map=orderDao.selectOnlyOrder(id,ino);
        return map;
    }

    //查询总页数
    @Override
    public List<Map<String, Object>> selectAllOrderNum(Integer id) {
        List<Map<String,Object>> list=orderDao.selectAllOrder(id,0,100000);
        return list;
    }



    //显示订单信息
    @Override
    public List<Map<String,Object>> selectAllOrder(Integer id,int start,int pagesize) throws JsonProcessingException {
        //先从redis缓存中查询  查询返回字符串 使用 lrange  TODO: 把这些功能整合进redisConfig中
        List<String> redisList=stringRedisTemplate.opsForList().range("AllOrder:"+id,start,start+pagesize-1);
        //使用布隆过滤器进行判断
        if (bloomFilter.contains(""+id)) {
            //定义一个List<Map> 集合切割接受redis数据
            List<Map<String, Object>> newList = new ArrayList<>();
            //redis中存在
            if (!redisList.isEmpty()) {
                //切割转为list<Map>结构
                for (int i = 0; i < redisList.size(); i++) {
                    newList.add(mapper.readValue(redisList.get(i), Map.class));
                }
                System.out.println("++++++++++++++++++++++++++++++++从缓存中查询++++++++++++++++++++++++++++++++");
                return newList;
            }
            //redis中不存在
            //从数据库查询
            List<Map<String, Object>> list = orderDao.selectAllOrder(id, start, pagesize);
            //再将数据存入缓存中,此时按照订单时间依次rpush进list集合中
            if (!list.isEmpty()) {
                for (Map<String, Object> data : list) {
                    stringRedisTemplate.opsForList().rightPush("AllOrder:" + id, mapper.writeValueAsString(data));
                    //设置过期时间 TODO:设计了随机过期时间,防止了缓存雪崩(后续可能加入其他技术,例如集群)
                    double RandomTime = Math.random() * 5 + 1;
                    stringRedisTemplate.expire("AllOrder:" + id, Duration.ofMinutes((long) RandomTime));   //设置过期时间
                    System.out.println("++++++++++++++++++++++++++++++++放入缓存中++++++++++++++++++++++++++++++++");
                }
                return list;
            }
        }
        //数据库查询不存在或布隆过滤器中查找不到相应的key
        return null;

    }

    //查询已收货订单
    @Override
    public List<Map<String, Object>> selectReceiveOrder(Integer id, int start, int pagesize) {
        List<Map<String,Object>> list=orderDao.selectReceiveOrder(id,start,pagesize);
        return list;
    }

    //查询未收货订单
    @Override
    public List<Map<String, Object>> selectUnReceiveOrder(Integer id, int start, int pagesize) {
        List<Map<String,Object>> list=orderDao.selectUnReceiveOrder(id,start,pagesize);
        return list;
    }

    //查询关闭订单
    @Override
    public List<Map<String, Object>> selectCloseOrder(Integer id, int start, int pagesize) {
        List<Map<String,Object>> list=orderDao.selectCloseOrder(id,start,pagesize);
        return list;
    }

    //更新用户信息
    @Override
    public int updateUserInfo(User user) {
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        if (!user.getUsername().isEmpty()){
            updateWrapper.eq("id",user.getId()).set("username",user.getUsername());
        }
        if (!user.getEmail().isEmpty()){
            updateWrapper.eq("id",user.getId()).set("email",user.getEmail());
        }
        if (!user.getTel().isEmpty()){
            updateWrapper.eq("id",user.getId()).set("tel",user.getTel());
        }
        if (!user.getPwd().isEmpty()){
            BCryptPasswordEncoder be=new BCryptPasswordEncoder();
            user.setPwd(be.encode(user.getPwd()));
            updateWrapper.eq("id",user.getId()).set("pwd",user.getPwd());
        }
       return userdao.update(null,updateWrapper);
    }

    //确认收货
    @Override
    public int confirmReceive(String ono) {
        UpdateWrapper<OrderInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("ono",ono).set("status","3");
        return  orderDao.update(null,updateWrapper);
    }

    //退款
    @Override
    public int refund(String ino) {
        UpdateWrapper<OrderItemInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("ino",ino).set("statu",0);
        return orderItemDao.update(null,updateWrapper);
    }

    //更新订单状态为已完成
    @Override
    public int updateStatus(String ono) {
        UpdateWrapper<OrderInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("ono",ono).set("status","1");
        return  orderDao.update(null,updateWrapper);
    }

    //根据uid查询地址
    @Override
    public List<Address> findAddressByUid(String uid,int start,int pagesize) {
       QueryWrapper<Address> wrapper=new QueryWrapper<>();
       wrapper.eq("uid",uid);
       List<Address> address=addressDao.findAddressByUid(uid,start,pagesize);
       return address;
    }

    //添加地址
    @Override
    public int saveAddress(String address, String uid, String tel, String consignee) {
        Address add=new Address();
        add.setAddress(address);
        add.setUid(Integer.parseInt(uid));
        add.setTel(tel);
        add.setConsignee(consignee);
        addressDao.insert(add);
        return 1;
    }

    @Override
    public User reg(User user) {
        userdao.insert(user);
        return user;
    }

//    //根据电话或姓名查询用户
//    @Override
//    public User findUserByUname(String username) {
//        QueryWrapper<User> wrapper=new QueryWrapper<>();
//        wrapper.eq("username",username);
//        User user=userdao.selectOne(wrapper);
//        return user;
//    }

    //根据电话查询用户
    @Override
    public List<User> findUserByTel(String tel) {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("tel",tel);
        List<User> list=userdao.selectList(wrapper);
        return list;
    }

    //根据邮箱查询用户
    @Override
    public List<User> findUserByEmail(String email) {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("email",email);
        List<User> list=userdao.selectList(wrapper);

        return list;
    }

    //更改密码
    @Override
    public int changePwd(String email,String pwd) {
        UpdateWrapper<User> updateWrapper= Wrappers.update();
        updateWrapper.eq("email",email);
        User user=new User();
        user.setPwd(pwd);
         int result= userdao.update(user,updateWrapper);
         return result;
    }



}