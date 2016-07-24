import json
import logging
import os
import sys
import argparse
import urllib.request
import time

FORMAT = '%(asctime)-15s - %(levelname)s - %(module)10s:%(lineno)-5d - %(message)s'
logging.basicConfig(stream=sys.stdout, level=logging.DEBUG, format=FORMAT)
LOG = logging.getLogger(__name__)


LOCATION_BLACKLIST = ['The House by Heineken', 'The Barbary']

class Event(object):
    def __init__(self, event_name, event_page, start_time, end_time):
        self.event_name = event_name
        self.event_page = event_page
        self.start_time = start_time
        self.end_time = end_time

    def __str__(self):
        return "(%s, %s, %s, %s)" % (self.event_name, self.event_page, self.start_time, self.end_time)

def read_schedule(files):
    schedules = []
    for file in files:
        with open(file) as data_file:    
            schedules.append(json.load(data_file))
    return schedules

def search_genre(name):
    search_url = 'https://api.spotify.com/v1/search?type=artist&q='+name.replace(' ', '%20')
    req = urllib.request.Request(search_url)
    search_res = None
    try:
        with urllib.request.urlopen(req) as response:
            if response.code == 200:
                search_res = json.loads(response.read().decode('utf-8'))
            else:
                LOG.info('Search failed for' + search_url + ' response: ' + response.code)
    except UnicodeEncodeError:
        LOG.info('UnicodeEncodeError for ' +  search_url)
    except urllib.error.HTTPError:
        LOG.info('HTTPError for ' +  search_url)
    time.sleep(0.1)
    return search_res

def parse_res(artist, res):
    if res:
        items = res['artists']['items']
        if items:
            genres = items[0]['genres']
            if genres:
                return genres
            else:
                return 'Empty genre'
        else:
            return 'No artist on Spotify'
    LOG.debug('No genre found for ' + artist)
    return 'Search error'

def main(args):
    schedules = read_schedule(args.input_names)
    events = [Event(**event) for one_day_schedule in schedules for location in one_day_schedule.keys() if location not in LOCATION_BLACKLIST for event in one_day_schedule[location]]
    genre_dict = {}

    for event in events:
        artist = event.event_name
        search_res = search_genre(artist)
        genre = parse_res(artist, search_res)
        genre_dict[artist] = genre

    

    with open('genres.json', 'w') as f:
        json.dump(genre_dict, f)





def parse_args():
    parser = argparse.ArgumentParser(description='Get the genre for each artist in the schedule')
    parser.add_argument('-i', '--input-names', type=str, nargs='+', help='Input names of the artist information')
    return parser.parse_args()

if __name__ == '__main__':
    main(parse_args())