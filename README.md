# Ananth's Anime Shelf

An animated, black-and-white shelf of everything on my [AniList](https://anilist.co/user/AnanthKrishna): anime, manga, manhwa and manhua.

It reads AniList live in the visitor's browser, so the page updates whenever my AniList changes. There's nothing to rebuild or redeploy.

## Features

- **Anime, Manga, Manhwa and Manhua** tabs, each with its own stats, charts and shelf
- Real AniList covers and banners
- Search, sort, status tabs, and genre and format filters
- Genre radar, my-score-vs-AniList bars, a card per genre, release years, score spread, formats and hot takes
- **Compare with a friend**: type any AniList username to see what you both have, what only you have and what only they have, plus a taste-match score
- **Watch orders**: hand-picked orders for Fate and Monogatari, automatic release orders for any other franchise (built from AniList's relations), with what's already watched ticked off
- **Search all of AniList**: find any anime or manga, see whether it's on the list, and open its details or watch order
- **Export**: save any part of the list (or everything) as a PDF, CSV or JSON
- **Switch account**: anyone can tap Switch and enter their own AniList username to make the whole site theirs (remembered on that device), or view anyone's shelf with `?user=<AniList name>`, and share a comparison with `?compare=<name>`

## Get the app

- **Android**: download [anime-shelf.apk](https://anime-shelf-seven.vercel.app/download/anime-shelf.apk) (also in this repo at [`download/anime-shelf.apk`](download/anime-shelf.apk) and on the [Releases page](https://github.com/AnanthKrishnaJ/anime-shelf/releases/latest)) and open it (Android 10 or newer). The app opens the live shelf full screen, saves exports to Downloads/Anime Shelf, and opens shared shelf links.
- **iPhone, iPad and desktop**: open the site and use *Add to Home Screen* (Safari) or *Install app* (Chrome and Edge).

The Android project lives in `android/`. Pushing changes there runs `.github/workflows/android.yml`, which builds a signed APK, publishes it as a new release and commits it to `download/anime-shelf.apk` so the website's download button always serves the newest version. The signing key is stored in the repository's Actions secrets (`KEYSTORE_B64`, `KEYSTORE_PASS`).

## Run it

It's a single static file, so open `index.html` or deploy the folder to any static host (it's set up for Vercel).

`data/` holds a saved copy of the list. The page only uses it if AniList can't be reached.

To show a different default profile, change `DEFAULT_USER` and `OWNER_NAME` at the top of the script in `index.html`.

Made by Ananth Krishna J.
