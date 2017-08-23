# app
my first app in Kotlin i was asked to code for the job interview

## Key "features"
1. loads questions from stackexchange
2. shows question detail
3. port/land screens, dual pane land screen for large xhdpi devices
4. "caches" results if allowed

"caches" here refers to nothing more than storing loaded data for future session (if no internet available) <br/>
the easiest option was to convert all data back to JSON and store in external storage, <br/>
therefore file is visible to anyone else and every store operation rewrites the whole file (:/) <br/>

the amount of data one could possibly "cache" this way is not limited

when store request comes in, it will compare:
1. first question id
2. last question id
3. timestamp when previous save occured

All of which are stored in shared prefs

The last one is to restrict SAVE operation to one per 10 sec, why not... <br/>
Current position in list is also stored, so next time it can get you to your previous position <br/>
10 sec rule does not apply for current pos in list

## Architecture?
Here i'am sticking to MVP, and the P which has nothing to do with anything android-specific <br/>
Pretty natural idea was to make RecyclerView Adapter as Presenter, but eventually all framework-related <br/>
stuff is either model or view

## Orientation change
i just let them all die and get re-created ofter Activity relaunch

## Some screens

<div style="display: inline-block">
  <img src="/screenshots/screen1.png?raw=true" alt="alt text" width="200" height="350">
</div>

<div style="display: block">
  <img src="/screenshots/screen4.png?raw=true" alt="alt text" width="200" height="350">
</div>


<div style="display: inline-block; word-spacing: 10">
  <img src="/screenshots/screen3.png?raw=true" alt="alt text" width="350" height="200">
</div>
<div style="margin-left:10"></div>

<div style="display: block;">
  <img src="/screenshots/screen2.png?raw=true" alt="alt text" width="350" height="200">
</div>

