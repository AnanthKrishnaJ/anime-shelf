# Ananth's Anime Shelf

An animated, black-and-white shelf of everything on my [AniList](https://anilist.co/user/AnanthKrishna): anime, manga, manhwa and manhua.

It reads AniList live in the visitor's browser, so the page updates whenever my AniList changes. There's nothing to rebuild or redeploy.

## Features

- **Anime, Manga, Manhwa and Manhua** tabs, each with its own stats, charts and shelf
- Real AniList covers and banners
- Search, sort, status tabs, and genre and format filters
- Genre radar, my-score-vs-AniList bars, a card per genre, release years, score spread, formats and hot takes
- **Compare with a friend**: type any AniList username to see what you both have, what only you have and what only they have, plus a taste-match score
- **Export**: save any part of the list (or everything) as a PDF, CSV or JSON
- View anyone's shelf with `?user=<AniList name>`, and share a comparison with `?compare=<name>`

## Run it

It's a single static file, so open `index.html` or deploy the folder to any static host (it's set up for Vercel).

`data/` holds a saved copy of the list. The page only uses it if AniList can't be reached.

To show a different default profile, change `DEFAULT_USER` and `OWNER_NAME` at the top of the script in `index.html`.

Made by Ananth Krishna J.
