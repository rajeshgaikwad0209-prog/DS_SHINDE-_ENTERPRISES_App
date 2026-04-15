package com.example.feastflow // Make sure this matches your package name!

data class Restaurant(val id: String, val name: String, val rating: Double, val deliveryTime: String, val cuisines: String, val location: String, val imageUrl: String)
data class MenuItem(val id: String, val name: String, val price: Int, val description: String, val isVeg: Boolean, val imageUrl: String)

fun getMockRestaurants() = listOf(
    Restaurant("1", "The Bombay Canteen", 4.5, "30-35 mins", "North Indian, Biryani", "Lower Parel", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQy307CZI-LrHBVTBoVqHkUqBXZHBEbjeB0ZA&s"),
    Restaurant("2", "Burger King", 4.2, "20-25 mins", "Burgers, American", "Andheri West", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_xC0qj7T9GgtN3H0B_VbttcTzAiFZH134ZA&s"),
    Restaurant("3", "Meghana Foods", 4.7, "40-45 mins", "Biryani, Andhra", "Bandra", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQVw3R9fCBAh3weelzYr9yniOyMjYeyyTQkjA&s")
)

fun getMockMenu() = listOf(
    MenuItem("1", "Paneer Butter Masala", 280, "Soft paneer cubes cooked in a rich tomato gravy.", true, "https://vegecravings.com/wp-content/uploads/2017/04/paneer-butter-masala-recipe-step-by-step-instructions.jpg"),
    MenuItem("2", "Chicken Dum Biryani", 320, "Authentic Hyderabadi biryani cooked slowly with spices.", false, "https://thespiceway.com/cdn/shop/files/chicken_dum_biryani.jpg?v=1710872833"),
    MenuItem("3", "Garlic Naan", 60, "Soft Indian flatbread topped with garlic and butter.", true, "https://allwaysdelicious.com/wp-content/uploads/2022/04/garlic-butter-naan-4.jpg")
)