import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wedify.GlobalNavigation
import com.example.wedify.model.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriesView(modifier: Modifier = Modifier) {
    val categoryList = remember { mutableStateOf<List<CategoryModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("stok")
            .collection("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull {
                    it.toObject(CategoryModel::class.java)
                }
                categoryList.value = list
            }
    }

    LazyRow(modifier = modifier.padding(horizontal = 16.dp)) {
        items(categoryList.value) { item ->
            CategoryItem(category = item)
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun CategoryItem(category: CategoryModel) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable {
                GlobalNavigation.navController.navigate("category-products/"+category.id)
            }
            .border(
                width = 2.dp,
                color = Color(0xFFFF69B4),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFEF4F4F4)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.nama,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = category.nama, textAlign = TextAlign.Center)
        }
    }
}
