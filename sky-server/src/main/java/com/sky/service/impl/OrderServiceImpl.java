package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;


    // 假设这是一个全局变量或通过其他方式传递的当前订单ID
    private String currentOrderId;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //业务异常检测(地址为空,购物车为空)
        //获取地址
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook== null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //获取当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(ShoppingCart.builder()
                .userId(userId)
                .build()
        );
        if(shoppingCartList== null || shoppingCartList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入数据
        Orders order = new Orders();

        BeanUtils.copyProperties(ordersSubmitDTO,order);
        //设置用户id
        order.setUserId(BaseContext.getCurrentId());
        //设置订单状态
        order.setStatus(Orders.PENDING_PAYMENT);
        //设置支付状态
        order.setPayStatus(Orders.UN_PAID);
        //设置下单时间
        order.setOrderTime(LocalDateTime.now());
        //生成订单号,订单号
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        //设置收货人
        order.setPhone(addressBook.getPhone());
        //设置收货地址
        order.setConsignee(addressBook.getConsignee());
        //设置收货地址
        order.setAddress(addressBook.getDetail());
        orderMapper.insert(order);

        List<OrderDetail> orderDetailList =new ArrayList<>();

        //向订单明细表插入n条数据
        shoppingCartList.forEach(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(order.getId());//设置订单id
            orderDetailList.add(orderDetail);
        });
        orderDetailMapper.insertBatch(orderDetailList);//批量插入

        //清空购物车
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        //封装一个OrderSubmitVO对象并返回
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .build();
        return build;
    }


    /**
     * 模拟支付处理
     * @param ordersPaymentDTO 支付参数（实际未使用）
     * @return 支付模拟结果
     */
    @Override
    @Transactional
    public OrderPaymentVO mockPayment(OrdersPaymentDTO ordersPaymentDTO) {
        // 1. 构造模拟的支付成功返回数据
        JSONObject mockResult = new JSONObject();
        mockResult.put("code", "ORDERPAID"); // 模拟微信返回的成功码
        mockResult.put("package", "prepay_id=mock_prepay_id_123456");

        // 2. 转换为前端需要的VO对象
        OrderPaymentVO paymentVO = mockResult.toJavaObject(OrderPaymentVO.class);
        // 注意：某些SDK需要单独设置package字段
        paymentVO.setPackageStr(mockResult.getString("package"));

        // 3. 定义支付后的状态
        Integer orderPaidStatus = Orders.PAID; // 支付状态：已支付
        Integer orderStatus = Orders.TO_BE_CONFIRMED; // 订单状态：待商家接单
        LocalDateTime checkoutTime = LocalDateTime.now(); // 支付时间

        currentOrderId = ordersPaymentDTO.getOrderNumber();
        // 4. 关键步骤：更新数据库订单状态
        if (!currentOrderId.isEmpty()) {
            orderMapper.updateStatus(orderStatus, orderPaidStatus, checkoutTime, currentOrderId);
        } else {
            // 日志记录或异常处理：未找到当前订单ID
            log.warn("未找到当前订单ID，无法更新订单状态");
        }

        // 5. 触发后续业务，如来单提醒（WebSocket）
//        sendNewOrderNotification(currentOrderId);

        return paymentVO;
    }
}
