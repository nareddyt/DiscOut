import json
import logging
import os
import sys
import argparse
from utils import *

FORMAT = '%(asctime)-15s - %(levelname)s - %(module)10s:%(lineno)-5d - %(message)s'
logging.basicConfig(stream=sys.stdout, level=logging.INFO, format=FORMAT)
LOG = logging.getLogger(__name__)


LOCATION_BLACKLIST = ['The House by Heineken', 'The Barbary']

class Event(object):
    def __init__(self, eventName, eventPage, eventImage, eventDate, startTime, endTime, location, previewUrl):
        self.eventName = eventName
        self.eventPage = eventPage
        self.eventImage = eventImage
        self.eventDate = eventDate
        self.startTime = startTime
        self.endTime = endTime
        self.location = location
        self.previewUrl = previewUrl

    def __str__(self):
        return "(%s, %s, %s, %s)" % (self.eventName, self.eventPage, self.startTime, self.endTime)

def read_schedule(file):
    schedules = []
    with open(file) as data_file:    
        return json.load(data_file)
    return schedules

def get_album_ids(artist_id):
    album_url = 'https://api.spotify.com/v1/artists/{}/albums'.format(artist_id)
    album_res = get_request(album_url)
    items = album_res['items'] if album_res else []
    album_ids = [item['id'] for item in items] if items else []
    return album_ids

def get_album_genres(album_id):
    album_genre_url = 'https://api.spotify.com/v1/albums/{}'.format(album_id)
    album_res = get_request(album_genre_url)
    album_genre = album_res['genres'] if album_res else []
    return album_genre    

def infer_genre(artist_id):
    album_ids = get_album_ids(artist_id)
    genres = []
    for album_id in album_ids:
        genres += get_album_genres(album_id)
    return genres

def main(args):
    schedules = read_schedule(args.input_names)
    events = [Event(**event) for one_day_schedule in schedules for location in one_day_schedule.keys() if location not in LOCATION_BLACKLIST for event in one_day_schedule[location]]
    genre_dict = {}
    for event in events:
        LOG.info('Getting genre for ' + event.eventName)
        artist = event.eventName
        search_res = search_artist(artist)
        genre = get_artist_genre(artist, search_res)
        genre_dict[artist] = genre

    inverted_genre_dict = {}
    for artist, genres in genre_dict.items():
        for genre in genres:
            for general_genre in genre.split(' '):
                if general_genre in inverted_genre_dict:
                    inverted_genre_dict[general_genre].append(artist)
                else:
                    inverted_genre_dict[general_genre] = [artist]
    with open('genres.json', 'w') as f:
        json.dump(inverted_genre_dict, f)


def parse_args():
    parser = argparse.ArgumentParser(description='Get the genre for each artist in the schedule')
    parser.add_argument('-i', '--input-names', type=str, help='Input names of the artist information')
    return parser.parse_args()

if __name__ == '__main__':
    main(parse_args())