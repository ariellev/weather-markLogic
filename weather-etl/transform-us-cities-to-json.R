require(dplyr)
require(jsonlite)

# manual steps:
# 1. download zip from http://download.geonames.org/export/dump/US.zip
# 2. extract zip
# 3. repalce tabs with semi-colon: cat US.txt | tr -s '\t' ';' > US.csv

d <- read.csv("US//US.csv", sep = ";", header = F, stringsAsFactors = F)
names(d) <- c("country", "postal", "place", "state", "state_code", "region", "region_code", "latitude", "longitude", "accuracy")
head(d)

places <- select(d, place, state, state_code, latitude, longitude)
places <- places[complete.cases(places), ]
head(places)
dim(places)

places <- places[which(!duplicated(places$place)),]
dim(places)

states_to_codes <- select(places, state, state_code)
states_to_codes <- states_to_codes[which(!duplicated(states_to_codes$state)),]

dim(states_to_codes)

x <- toJSON(places, pretty = T)
write(x, "US/places.json")

x <- toJSON(states_to_codes, pretty = T)
write(x, "US/states_to_codes.json")
