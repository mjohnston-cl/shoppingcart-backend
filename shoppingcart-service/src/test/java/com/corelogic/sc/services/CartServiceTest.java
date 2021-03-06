package com.corelogic.sc.services;

import com.corelogic.sc.entities.Cart;
import com.corelogic.sc.entities.Item;
import com.corelogic.sc.entities.Product;
import com.corelogic.sc.entities.ProductCategory;
import com.corelogic.sc.exceptions.CartNotFoundException;
import com.corelogic.sc.exceptions.ItemNotFoundException;
import com.corelogic.sc.exceptions.ProductNotFoundException;
import com.corelogic.sc.requests.AddCartRequest;
import com.corelogic.sc.requests.DeleteCartRequest;
import com.corelogic.sc.requests.RemoveItemFromCartRequest;
import com.corelogic.sc.responses.CartResponse;
import com.corelogic.sc.responses.CartStatus;
import com.corelogic.sc.responses.ItemResponse;
import com.corelogic.sc.respositories.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class CartServiceTest {

    @Mock
    private CartRepository mockCartRepository;

    @Mock
    private ItemService mockItemService;

    private CartService subject;

    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setUp() throws Exception {
        subject = new CartService(mockCartRepository, mockItemService);
    }

    @Test
    public void createCart_createsCart() {
        Cart savedCart = Cart.builder().cartName("Cart101").description("MyFirstCart").status("ACTIVE").build();
        when(mockCartRepository.save(Cart.builder().cartName("Cart101").description("MyFirstCart").status("ACTIVE").build())).thenReturn(savedCart);

        CartResponse actual = subject.createCart(AddCartRequest.builder().cartName("Cart101").description("MyFirstCart").build());

        CartResponse expected = CartResponse.builder().cartName("Cart101").description("MyFirstCart").status(CartStatus.ACTIVE).build();
        verify(mockCartRepository).save(Cart.builder().cartName("Cart101").description("MyFirstCart").status("ACTIVE").build());
        assertEquals(expected, actual);
    }

    @Test
    public void findCart_findsCartByCartName() throws Exception {
        Cart savedCart = Cart.builder().cartName("Cart101").description("MyFirstCart").status("ACTIVE").build();
        when(mockCartRepository.findByCartName("Cart101")).thenReturn(savedCart);

        CartResponse actual = subject.findCart("Cart101");

        CartResponse expected = CartResponse.builder().cartName("Cart101").description("MyFirstCart").status(CartStatus.ACTIVE).build();
        verify(mockCartRepository).findByCartName("Cart101");
        assertEquals(expected, actual);
    }

    @Test
    public void findCart_doesNotFindCartByThatCartName_throwsCartNotFoundException() throws Exception {
        when(mockCartRepository.findByCartName("Cart101")).thenReturn(null);

        Assertions.assertThrows(CartNotFoundException.class, () ->
                subject.findCart("Cart101"));

        verify(mockCartRepository).findByCartName("Cart101");
    }

    @Test
    public void deleteCart_deletesCart() throws CartNotFoundException, ProductNotFoundException, ItemNotFoundException {
        Product product = Product
                .builder()
                .price(100.0)
                .productName("IPAD10")
                .description("IPAD10")
                .inventoryCount(100)
                .skuNumber("IPAD10")
                .productCategory(ProductCategory.builder().build())
                .build();

        Cart savedCart = Cart
                .builder()
                .cartName("MyFirstCart")
                .description("MyFirstCart")
                .status("ACTIVE")
                .items(Collections.singletonList(Item
                        .builder()
                        .itemId(1L)
                        .quantity(1)
                        .createdDate(now)
                        .product(product)
                        .build()))
                .build();
        when(mockCartRepository.findByCartName("MyFirstCart")).thenReturn(savedCart);

        doNothing().when(mockCartRepository).deleteById("MyFirstCart");

        subject.deleteCart(DeleteCartRequest
                .builder()
                .cartName("MyFirstCart")
                .build());
        verify(mockCartRepository).deleteById("MyFirstCart");
    }

    @Test
    public void deleteCart_invokesItemService() throws CartNotFoundException, ProductNotFoundException, ItemNotFoundException {
        Product product = Product
                .builder()
                .price(100.0)
                .productName("IPAD10")
                .description("IPAD10")
                .inventoryCount(100)
                .skuNumber("IPAD10")
                .productCategory(ProductCategory.builder().build())
                .build();

        Cart savedCart = Cart
                .builder()
                .cartName("MyFirstCart")
                .description("MyFirstCart")
                .status("ACTIVE")
                .items(Collections.singletonList(Item
                        .builder()
                        .itemId(1L)
                        .quantity(1)
                        .createdDate(now)
                        .product(product)
                        .build()))
                .build();
        when(mockCartRepository.findByCartName("MyFirstCart")).thenReturn(savedCart);

        RemoveItemFromCartRequest removeItemFromCartRequest = RemoveItemFromCartRequest
                .builder()
                .cartName("MyFirstCart")
                .quantity(1)
                .skuNumber("IPAD10")
                .build();
        when(mockItemService.removeItem(removeItemFromCartRequest)).thenReturn(ItemResponse.builder().build());

        doNothing().when(mockCartRepository).deleteById("MyFirstCart");

        subject.deleteCart(DeleteCartRequest
                .builder()
                .cartName("MyFirstCart")
                .build());

        verify(mockItemService).removeItem(RemoveItemFromCartRequest
                .builder()
                .cartName("MyFirstCart")
                .quantity(1)
                .skuNumber("IPAD10")
                .build());
    }

    @Test
    public void deleteCart_doesNotFindCartByThatCartName_throwsCartNotFoundException() {
        when(mockCartRepository.findByCartName("InvalidCart")).thenReturn(null);

        Assertions.assertThrows(CartNotFoundException.class, () ->
                subject.deleteCart(DeleteCartRequest.builder().cartName("InvalidCart").build()));
    }
}
