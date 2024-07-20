package itbank.pethub.model;

import itbank.pethub.vo.*;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDAO {
    @Insert("Insert into delivery(address, post, status_id) values (#{address}, #{post},1)")
    int makedelivery(DeliveryVO dsv);

    @Select("SELECT id FROM delivery ORDER BY id DESC LIMIT 1")
    int getdelivery_id();

    @Insert("INSERT INTO `order` (member_id, delivery_id, order_status) VALUES (#{member_id}, #{delivery_id}, 1)")
    int makeOrder(OrderVO ov);

    @Select("select * from item where id=#{productId}")
    ItemVO getItem(int productId);

    @Select("SELECT id FROM `order` ORDER BY id DESC LIMIT 1")
    int getorderid();

    @Insert("insert into cart (order_id, order_item, order_price, count, origin_price, item_name, item_pic) values (#{order_id}, #{order_item}, #{order_price}, #{count}, #{origin_price}, #{item_name}, #{item_pic})")
    int makecart(CartVO cv);

    @Update("UPDATE cart SET count = count + #{count} WHERE id = #{id} AND order_id IN (SELECT o.id FROM `order` o " +
            "WHERE o.order_status = (SELECT os.id FROM order_status os WHERE os.name = '주문 접수'))")
    int countup(CartVO cartVO);

    @Select("SELECT c.* FROM cart c INNER JOIN `order` o ON c.order_id = o.id WHERE o.member_id = #{memberId} AND o.order_status = 1")
    public List<CartVO> getCarts(int memberId);

    @Select("select * from item order by id desc")
    List<ItemVO> selectAll();

    @Select("SELECT * FROM item WHERE type = #{type} AND category  = #{category} ORDER BY id DESC")
    List<ItemVO> category(int category, int type);

    @Select("select * from item where id = #{id}")
    ItemVO selectOne(int id);


    @Select("SELECT id FROM cart WHERE order_id IN (SELECT id FROM `order` WHERE member_id=#{memberId} and order_status in (select id from order_status where name ='주문 접수')) AND order_item=#{id}")
    @ResultType(Integer.class)
    Integer getExistingOrderId(@Param("memberId") int memberId, @Param("id") int id);

    @Select("select * from cart where id=#{id}")
    CartVO selectCart(int id);

    @Delete("Delete From cart where order_id=#{orderId}")
    int deleteCart(int orderId);

    @Select("select delivery_id from `order` where id=#{order_id}")
    int getDeli_id(int order_id);

    @Delete("DELETE from `order` where id=#{orderId}")
    int deleteOrder(int orderId);

    @Update("UPDATE cart SET count = #{count} WHERE id = #{id}")
    void updateCart(@Param("count") int count, @Param("id") int id);


    @Delete("DELETE from delivery where id=#{dId}")
    int deleteDelivery(int dId);


    @Select("select * from modc where member_id=#{memberId} and order_status = '주문 접수'")
    List<MODCVO> selectMODC(int memberId);

    @Select("SELECT * from address where member_id=#{memberId}")
    AddressVO getAddress(int memberId);

    @Update("UPDATE delivery SET address = #{delivery_address}, post = #{delivery_post} WHERE id = #{delivery_id}")
    int addressupdate(MODCVO user);

    @Select("select * from modc where member_id=#{memberId} and order_status != '주문 접수'")
    List<MODCVO> selectAfterpay(int memberId);

    @Select("SELECT * FROM modc WHERE member_id=#{memberId} AND order_status != '주문 접수'ORDER BY order_date DESC LIMIT 1")
    List<MODCVO> ordercheck(int memberId);

    @Update("update `order` set order_status=2 where id=#{orderId}")
    int updateorder(int orderId);

    @Select("SELECT * FROM modc WHERE member_id=#{memberid} AND order_status='주문 접수' ORDER BY order_date DESC LIMIT 1")
    MODCVO getcartid(int memberid);

    @Insert("insert into cart (order_id, order_item, order_price, count, origin_price, cart_deperate_id, item_name, item_pic) values (#{order_id}, #{order_item}, #{order_price}, #{count}, #{origin_price}, #{cart_deperate_id}, #{item_name}, #{item_pic})")
    int makeCartid(CartVO cv);

    @Select("select count(*) from modc where member_id=#{memberid} and order_status='주문 접수'")
    int getexistingcartid(int memberId);

    @Delete("DELETE FROM cart WHERE cart_deperate_id = #{cartItemId}")
    int deleteAllCart(int cartItemId);

    @Select("SELECT order_id FROM cart WHERE cart_deperate_id = #{cartItemId} GROUP BY order_id")
    List<Integer> getOrderIds(int cartItemId);


    @Select("SELECT order_id FROM cart WHERE cart_deperate_id = #{cartItemId} bGROUP BY order_id")
    int getorder_id(int cartItemId);
}