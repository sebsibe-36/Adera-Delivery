package com.example.data

import com.example.R
import com.example.model.Category
import com.example.model.FoodItem
import com.example.model.OrderStatus
import com.example.model.Restaurant
import com.example.model.Order
import com.example.model.CartItem

object SampleData {

    val categories = listOf(
        Category("cat_all", "All Foods", "ሁሉንም", "restaurant", 16),
        Category("cat_traditional", "Traditional", "ባህላዊ", "lunch_dining", 6),
        Category("cat_meat", "Meat Dishes", "የስጋ", "kebab_dining", 5),
        Category("cat_fasting", "Fasting / Vegan", "የፆም", "eco", 4),
        Category("cat_breakfast", "Breakfast", "ቁርስ", "bakery_dining", 3),
        Category("cat_drinks", "Beverages & Buna", "መጠጥ", "local_cafe", 3)
    )

    val deliveryLocations = listOf(
        "Bole Medhanialem, Addis Ababa",
        "Kazanchis, Addis Ababa",
        "Sarbet / Vatican Embassy, Addis Ababa",
        "Old Airport (Bisrate Gabriel), Addis Ababa",
        "CMC / Summit Road, Addis Ababa",
        "Piazza / Arada, Addis Ababa",
        "Gerji Mebrat Hail, Addis Ababa"
    )

    val restaurants = listOf(
        Restaurant(
            id = "rest_1",
            name = "Addis Habesha Kitchen",
            amharicName = "አዲስ ሐበሻ ኩሽና",
            cuisine = "Authentic Ethiopian Traditional",
            rating = 4.9,
            reviewCount = 340,
            deliveryTimeMinutes = 25,
            deliveryFee = 50.0,
            minOrder = 200.0,
            address = "Bole Road, behind Edna Mall",
            coverDrawableRes = R.drawable.promo_banner,
            tags = listOf("Traditional", "Doro Wat", "Top Rated"),
            isFeatured = true
        ),
        Restaurant(
            id = "rest_2",
            name = "Yod Abyssinia Gourmet",
            amharicName = "ዮድ አቢሲኒያ",
            cuisine = "Heritage Feasts & Cultural Dishes",
            rating = 4.8,
            reviewCount = 512,
            deliveryTimeMinutes = 35,
            deliveryFee = 60.0,
            minOrder = 250.0,
            address = "Bole Medhanialem next to Brass Clinic",
            coverDrawableRes = R.drawable.dish_tibs,
            tags = listOf("Agelgil", "Tibs", "Beyaynet"),
            isFeatured = true
        ),
        Restaurant(
            id = "rest_3",
            name = "Kategna Traditional Lounge",
            amharicName = "ካቴኛ ባህላዊ ምግብ ቤት",
            cuisine = "Classic Ethiopian Gourmet",
            rating = 4.7,
            reviewCount = 280,
            deliveryTimeMinutes = 30,
            deliveryFee = 50.0,
            minOrder = 180.0,
            address = "Kazanchis, Inter Luxury area",
            coverDrawableRes = R.drawable.dish_doro_wat,
            tags = listOf("Kitfo", "Firfir", "Best Shiro"),
            isFeatured = true
        ),
        Restaurant(
            id = "rest_4",
            name = "Lucy Ethio-Fasting & Grill",
            amharicName = "ሉሲ የፆም እና ባህላዊ",
            cuisine = "Fresh Fasting & Derek Tibs",
            rating = 4.8,
            reviewCount = 190,
            deliveryTimeMinutes = 20,
            deliveryFee = 45.0,
            minOrder = 150.0,
            address = "National Museum St, Amist Kilo",
            coverDrawableRes = R.drawable.promo_banner,
            tags = listOf("Vegan / Fasting", "Shiro", "Healthy")
        ),
        Restaurant(
            id = "rest_5",
            name = "Bole Habesha Special Tibs House",
            amharicName = "ቦሌ ልዩ ጥብስ ቤት",
            cuisine = "Prime Cut Beef & Shekla Tibs",
            rating = 4.6,
            reviewCount = 165,
            deliveryTimeMinutes = 30,
            deliveryFee = 55.0,
            minOrder = 220.0,
            address = "Bole Atlas, Namibian St",
            coverDrawableRes = R.drawable.dish_tibs,
            tags = listOf("Shekla Tibs", "Kuanta Firfir", "Grill")
        ),
        Restaurant(
            id = "rest_6",
            name = "Tomoca Coffee & Morning Delights",
            amharicName = "ቶሞካ ቡና እና ቁርስ",
            cuisine = "Addis Artisan Coffee & Chechebsa",
            rating = 4.9,
            reviewCount = 680,
            deliveryTimeMinutes = 15,
            deliveryFee = 40.0,
            minOrder = 120.0,
            address = "Wavel St, Churchill Ave",
            coverDrawableRes = R.drawable.promo_banner,
            tags = listOf("Coffee", "Chechebsa", "Breakfast")
        )
    )

    val foodItems = listOf(
        FoodItem(
            id = "food_doro_wat",
            restaurantId = "rest_1",
            restaurantName = "Addis Habesha Kitchen",
            name = "Doro Wat Special",
            amharicName = "ዶሮ ወጥ በሙሉ ዕንቁላል",
            description = "Rich, slow-cooked traditional chicken drumstick simmered in aromatic berbere sauce, caramelized onions, nit'ir qibe spiced butter, and a boiled egg. Served with fresh teff injera.",
            price = 450.0,
            categoryId = "cat_traditional",
            drawableRes = R.drawable.dish_doro_wat,
            rating = 4.9,
            reviewCount = 210,
            spicyLevel = 3,
            isVegetarian = false,
            isPopular = true,
            preparationTime = "25-35 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_beyaynet",
            restaurantId = "rest_2",
            restaurantName = "Yod Abyssinia Gourmet",
            name = "Grand Fasting Beyaynet",
            amharicName = "የጾም በያይነት (12 ዓይነት)",
            description = "Vibrant vegetarian rainbow platter arranged on fresh injera: yellow split pea Kik Alicha, spicy red lentil Misir Wat, savory Shiro, collard greens (Gomen), beetroot, and salad.",
            price = 280.0,
            categoryId = "cat_fasting",
            drawableRes = R.drawable.promo_banner,
            rating = 4.9,
            reviewCount = 312,
            spicyLevel = 1,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "15-20 min",
            portionInfo = "Generous Platter (Serves 2)"
        ),
        FoodItem(
            id = "food_shekla_tibs",
            restaurantId = "rest_5",
            restaurantName = "Bole Habesha Special Tibs House",
            name = "Sizzling Shekla Tibs",
            amharicName = "የሸክላ የበሬ ጥብስ",
            description = "Prime tender beef cubes sautéed with fresh rosemary, garlic, red onions, and sliced jalapeño peppers. Served bubbling hot over a charcoal clay warmer with mitmita and awaze.",
            price = 420.0,
            categoryId = "cat_meat",
            drawableRes = R.drawable.dish_tibs,
            rating = 4.8,
            reviewCount = 188,
            spicyLevel = 2,
            isVegetarian = false,
            isPopular = true,
            preparationTime = "20-25 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_special_kitfo",
            restaurantId = "rest_3",
            restaurantName = "Kategna Traditional Lounge",
            name = "Special Gurage Kitfo",
            amharicName = "የጉራጌ ልዩ ክትፎ በቆጮ",
            description = "Freshly minced lean beef infused with aromatic niter kibbeh (clarified spiced butter) and fiery mitmita. Accompanied by ayib (fresh cottage cheese), gomen, and kocho bread.",
            price = 480.0,
            categoryId = "cat_meat",
            drawableRes = R.drawable.dish_doro_wat,
            rating = 4.9,
            reviewCount = 265,
            spicyLevel = 2,
            isVegetarian = false,
            isPopular = true,
            preparationTime = "15-20 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_shiro_tegabino",
            restaurantId = "rest_4",
            restaurantName = "Lucy Ethio-Fasting & Grill",
            name = "Shiro Tegabino Clay Pot",
            amharicName = "ተጋቢኖ ሽሮ በሸክላ",
            description = "Thick, silky, bubbling stew of powdered chickpeas cooked with garlic, shallots, and spicy berbere, served sputtering in an authentic earthen clay pot with rolls of soft injera.",
            price = 220.0,
            categoryId = "cat_fasting",
            drawableRes = R.drawable.promo_banner,
            rating = 4.7,
            reviewCount = 154,
            spicyLevel = 2,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "15-20 min",
            portionInfo = "Serves 1"
        ),
        FoodItem(
            id = "food_chechebsa",
            restaurantId = "rest_6",
            restaurantName = "Tomoca Coffee & Morning Delights",
            name = "Special Chechebsa (Kita Firfir)",
            amharicName = "ቂጣ ፍርፍር / ጨጨብሳ በማር",
            description = "Crispy, hand-torn shredded unleavened flatbread fried in spiced butter (kibbeh) and berbere, drizzled with pure highland honey. Served with a side of creamy fresh yogurt.",
            price = 190.0,
            categoryId = "cat_breakfast",
            drawableRes = R.drawable.dish_tibs,
            rating = 4.8,
            reviewCount = 98,
            spicyLevel = 1,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "10-15 min",
            portionInfo = "Breakfast Bowl"
        ),
        FoodItem(
            id = "food_kuanta_firfir",
            restaurantId = "rest_1",
            restaurantName = "Addis Habesha Kitchen",
            name = "Kuanta Firfir Special",
            amharicName = "የቋንጣ ፍርፍር በቅቤ",
            description = "Sun-dried seasoned cured beef jerky shreds stewed in a bold berbere and tomato gravy, tossed together with shredded pieces of soft teff injera. Hearty and savory.",
            price = 380.0,
            categoryId = "cat_traditional",
            drawableRes = R.drawable.dish_doro_wat,
            rating = 4.8,
            reviewCount = 142,
            spicyLevel = 2,
            isVegetarian = false,
            isPopular = false,
            preparationTime = "20-25 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_bozena_shiro",
            restaurantId = "rest_3",
            restaurantName = "Kategna Traditional Lounge",
            name = "Bozena Shiro with Beef",
            amharicName = "ቦዘና ሽሮ በስጋ",
            description = "Classic seasoned chickpea shiro stew enriched with tender bites of slow-cooked beef and finished with rich spiced kibbeh. Comfort food at its finest.",
            price = 320.0,
            categoryId = "cat_meat",
            drawableRes = R.drawable.promo_banner,
            rating = 4.7,
            reviewCount = 110,
            spicyLevel = 2,
            isVegetarian = false,
            isPopular = false,
            preparationTime = "18-25 min",
            portionInfo = "Serves 1"
        ),
        FoodItem(
            id = "food_derek_tibs",
            restaurantId = "rest_5",
            restaurantName = "Bole Habesha Special Tibs House",
            name = "Crispy Derek Tibs",
            amharicName = "ደረቅ ጥብስ በቃሪያ",
            description = "Lean cut beef pan-fried until crisp on the edges, tossed with caramelized red onions, roasted garlic, and green chilies. Served with warm rolls of injera.",
            price = 440.0,
            categoryId = "cat_meat",
            drawableRes = R.drawable.dish_tibs,
            rating = 4.8,
            reviewCount = 175,
            spicyLevel = 3,
            isVegetarian = false,
            isPopular = true,
            preparationTime = "20-25 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_gomen_besiga",
            restaurantId = "rest_2",
            restaurantName = "Yod Abyssinia Gourmet",
            name = "Gomen Besiga (Collard Greens & Beef)",
            amharicName = "ጎመን በስጋ",
            description = "Tender collard greens simmered with slow-braised beef ribs, ginger, garlic, and cardamom butter. Mildly spiced and packed with wholesome flavor.",
            price = 340.0,
            categoryId = "cat_traditional",
            drawableRes = R.drawable.dish_doro_wat,
            rating = 4.6,
            reviewCount = 85,
            spicyLevel = 1,
            isVegetarian = false,
            isPopular = false,
            preparationTime = "20-30 min",
            portionInfo = "Serves 1-2"
        ),
        FoodItem(
            id = "food_agelgil_combo",
            restaurantId = "rest_2",
            restaurantName = "Yod Abyssinia Gourmet",
            name = "Royal Agelgil Feast Box",
            amharicName = "የንጉስ አገልግል ጥምር",
            description = "Wrapped in traditional banana leaves and parchment inside a woven basket: layers of injera loaded with Doro Wat, Minced Kitfo, Derek Tibs, Ayib cheese, and Boiled Eggs.",
            price = 780.0,
            categoryId = "cat_traditional",
            drawableRes = R.drawable.promo_banner,
            rating = 5.0,
            reviewCount = 380,
            spicyLevel = 2,
            isVegetarian = false,
            isPopular = true,
            preparationTime = "30-40 min",
            portionInfo = "Family Feast (Serves 3-4)"
        ),
        FoodItem(
            id = "food_sambusa",
            restaurantId = "rest_4",
            restaurantName = "Lucy Ethio-Fasting & Grill",
            name = "Crispy Lentil & Herb Sambusa (4pcs)",
            amharicName = "ሳምቡሳ በምስር (4 ፍሬ)",
            description = "Golden triangular pastries stuffed with savory spiced brown lentils, shallots, and green chili peppers. Deep-fried until crunchy perfection.",
            price = 140.0,
            categoryId = "cat_fasting",
            drawableRes = R.drawable.dish_tibs,
            rating = 4.8,
            reviewCount = 142,
            spicyLevel = 1,
            isVegetarian = true,
            isPopular = false,
            preparationTime = "10-15 min",
            portionInfo = "4 Pieces"
        ),
        FoodItem(
            id = "food_genfo",
            restaurantId = "rest_6",
            restaurantName = "Tomoca Coffee & Morning Delights",
            name = "Traditional Genfo with Niter Kibbeh",
            amharicName = "የገብስ ገንፎ በቅቤ እና በርበሬ",
            description = "Dense roasted barley porridge well filled with bubbling hot spiced butter and berbere sauce, bordered with cool natural yogurt.",
            price = 210.0,
            categoryId = "cat_breakfast",
            drawableRes = R.drawable.dish_doro_wat,
            rating = 4.7,
            reviewCount = 67,
            spicyLevel = 2,
            isVegetarian = true,
            isPopular = false,
            preparationTime = "15-20 min",
            portionInfo = "Hearty Breakfast"
        ),
        FoodItem(
            id = "food_injera_extra",
            restaurantId = "rest_1",
            restaurantName = "Addis Habesha Kitchen",
            name = "Pure Teff Injera Roll (Pack of 3)",
            amharicName = "የነጭ ጤፍ እንጀራ (3 ፍሬ)",
            description = "100% gluten-free premium white teff fermented flatbread with distinctive spongy eyes (ayen), freshly prepared daily.",
            price = 90.0,
            categoryId = "cat_traditional",
            drawableRes = R.drawable.promo_banner,
            rating = 4.9,
            reviewCount = 520,
            spicyLevel = 0,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "5 min",
            portionInfo = "Pack of 3 rolls"
        ),
        FoodItem(
            id = "food_ethio_coffee",
            restaurantId = "rest_6",
            restaurantName = "Tomoca Coffee & Morning Delights",
            name = "Jebena Buna Flask & Roasted Beans",
            amharicName = "የጀበና ቡና በፍላስክ",
            description = "Freshly roasted Ethiopian Yirgacheffe coffee brewed in traditional clay Jebena, delivered hot in an insulated flask with a side of popped popcorn (fendisha).",
            price = 120.0,
            categoryId = "cat_drinks",
            drawableRes = R.drawable.promo_banner,
            rating = 5.0,
            reviewCount = 440,
            spicyLevel = 0,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "10-15 min",
            portionInfo = "Serves 3-4 cups"
        ),
        FoodItem(
            id = "food_avocado_spris",
            restaurantId = "rest_4",
            restaurantName = "Lucy Ethio-Fasting & Grill",
            name = "Fresh Layered Spris (Avocado & Mango)",
            amharicName = "ስፕሪስ ጁስ (አቮካዶ እና ማንጎ)",
            description = "Thick, fresh blended tropical avocado, sweet mango and papaya juice layers garnished with lime and vimto drizzle. Pure natural energy.",
            price = 150.0,
            categoryId = "cat_drinks",
            drawableRes = R.drawable.dish_tibs,
            rating = 4.9,
            reviewCount = 230,
            spicyLevel = 0,
            isVegetarian = true,
            isPopular = true,
            preparationTime = "5-10 min",
            portionInfo = "500ml Bottle"
        )
    )

    val sampleInitialOrders = listOf(
        Order(
            id = "ord_101",
            orderNumber = "AD-7892",
            items = listOf(
                CartItem(
                    foodItem = foodItems[0], // Doro Wat
                    quantity = 1,
                    spiceLevel = "Medium"
                ),
                CartItem(
                    foodItem = foodItems[13], // Pure Teff Injera
                    quantity = 1
                )
            ),
            subtotal = 540.0,
            deliveryFee = 50.0,
            total = 590.0,
            status = OrderStatus.PREPARING,
            customerName = "Selamawit Tadesse",
            customerPhone = "+251 91 234 5678",
            deliveryAddress = "Bole Medhanialem, Bldg 4, Apt 2B",
            deliveryNotes = "Ring bell on gate, extra napkins please",
            paymentMethod = "Cash on Delivery",
            estimatedArrival = "20-25 mins",
            driverName = "Dawit Girma",
            driverPhone = "+251 92 888 9900"
        ),
        Order(
            id = "ord_102",
            orderNumber = "AD-6510",
            items = listOf(
                CartItem(
                    foodItem = foodItems[2], // Shekla Tibs
                    quantity = 2,
                    spiceLevel = "Hot"
                ),
                CartItem(
                    foodItem = foodItems[14], // Jebena Buna
                    quantity = 1
                )
            ),
            subtotal = 960.0,
            deliveryFee = 50.0,
            total = 1010.0,
            status = OrderStatus.DELIVERED,
            customerName = "Selamawit Tadesse",
            customerPhone = "+251 91 234 5678",
            deliveryAddress = "Bole Medhanialem, Bldg 4, Apt 2B",
            paymentMethod = "Cash on Delivery",
            driverName = "Abebe Kebede",
            driverPhone = "+251 91 123 4567"
        )
    )
}
