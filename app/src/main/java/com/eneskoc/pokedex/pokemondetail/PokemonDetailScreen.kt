package com.eneskoc.pokedex.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.eneskoc.pokedex.R
import com.eneskoc.pokedex.data.remote.responses.Pokemon
import com.eneskoc.pokedex.data.remote.responses.Type
import com.eneskoc.pokedex.util.Resource
import com.eneskoc.pokedex.util.parseStatToAbbr
import com.eneskoc.pokedex.util.parseStatToColor
import com.eneskoc.pokedex.util.parseTypeToColor
import java.util.*


@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 90.dp,
    pokemonImageSize: Dp = 250.dp,
    viewModel: PokemonDetailViewmodel = hiltViewModel()
) {
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }.value
    val scrollState = rememberScrollState()
    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 10.dp)
            .verticalScroll(scrollState)

    ) {
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )


        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            dominantColor = dominantColor,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pokeball_icon),
                contentDescription = "Background Image",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 30.dp,top=80.dp)
                    .size(210.dp)
                    .alpha(0.2f)
            )

            if (pokemonInfo is Resource.Success) {
                pokemonInfo.data?.sprites?.let {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.front_default)
                            .build(),
                        contentDescription = pokemonInfo.data.name,
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding),
                        loading = {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.scale(0.5F)
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,

        ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    dominantColor: Color,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                dominantColor = dominantColor,
                modifier = Modifier
                    .offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }

    }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    dominantColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 95.dp)
    )
    {
        Text(
            text = "#${pokemonInfo.id}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 25.dp)
        )

        Text(
            text = "${pokemonInfo.name.capitalize(Locale.ROOT)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 25.dp)
        )

        PokemonTypeSection(types = pokemonInfo.types)
        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 75.dp, start = 25.dp, end = 25.dp)
            ) {
                PokemonBaseStats(pokemonInfo = pokemonInfo, dominantColor = dominantColor)
            }
        }
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Column(
        //horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(start = 25.dp)
    ) {
        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonStats(
    statsName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 20.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = statsName,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.width(15.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(height)
                .clip(CircleShape)
                .background(
                    if (isSystemInDarkTheme()) {
                        Color(0xFF505050)
                    } else {
                        Color.LightGray
                    }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(curPercent.value)
                    .clip(CircleShape)
                    .background(statColor)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = (curPercent.value * statMaxValue).toInt().toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    dominantColor: Color,
    animDelayPerItem: Int = 100
) {
    val maxBaseStats = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Base Stats",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = dominantColor
        )
        Spacer(modifier = Modifier.height(15.dp))

        for (i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonStats(
                statsName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStats,
                statColor = dominantColor,
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(15.dp))
        }

        Spacer(modifier = Modifier.height(150.dp))
    }
}
