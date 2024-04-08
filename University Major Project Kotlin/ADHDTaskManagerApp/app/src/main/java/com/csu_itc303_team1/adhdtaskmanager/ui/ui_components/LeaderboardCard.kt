package com.csu_itc303_team1.adhdtaskmanager.ui.ui_components

// Import CardElevation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Users


val LeaderboardBlue = Color(0xFF045EA5)


@Composable
fun LeaderboardCard(user: Users, rank: Int, defaultProfileImageUrl: String?) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .fillMaxWidth()
            .background(Color.White),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically  // Vertically centering the content
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.weight(0.8f),
                color = LeaderboardBlue,
                fontSize = 18.sp
            )

            val imageUrlToDisplay = user.profileImage ?: defaultProfileImageUrl
            imageUrlToDisplay?.let { url ->
                Image(
                    painter = rememberImagePainter(data = url),
                    contentDescription = "User's Mascot",
                    modifier = Modifier
                        .size(105.dp)
                        .padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }

            Text(
                text = user.username ?: "",
                modifier = Modifier.weight(2f),
                color = Color.Black,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic // Added a cursive style for a playful look
            )
            Text(
                text = user.country ?: "",
                modifier = Modifier.weight(2f),
                color = Color.Black,
                fontSize = 18.sp
            )
            Text(
                text = ((user?.points ?: 0) + (user?.loginNum ?: 0)).toString(),
                modifier = Modifier.weight(1f),
                color = LeaderboardBlue,
                fontSize = 18.sp,
                textAlign = TextAlign.End
            )
        }
    }
}