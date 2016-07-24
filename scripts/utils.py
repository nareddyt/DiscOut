import logging
import sys
import urllib.request
import json
import time

FORMAT = '%(asctime)-15s - %(levelname)s - %(module)10s:%(lineno)-5d - %(message)s'
logging.basicConfig(stream=sys.stdout, level=logging.INFO, format=FORMAT)
LOG = logging.getLogger(__name__)


def search_artist(artist_name):
    search_url = 'https://api.spotify.com/v1/search?type=artist&q='+artist_name.replace(' ', '%20')
    return get_request(search_url)

def get_top_tracks(artist_id, country):
    top_track_url = 'https://api.spotify.com/v1/artists/{}/top-tracks?country={}'.format(artist_id, country)
    return get_request(top_track_url)

def get_preview_url(top_track_res):
    if top_track_res:
        tracks = top_track_res['tracks']
        if tracks:
            top_track = tracks[0]
            return top_track['preview_url']
    return ''

def get_artist_id(artist_name, artist_info):
    if artist_info:
        items = artist_info['artists']['items']
        if items:
            artist = items[0]
            artist_id = artist['id']
            if artist_id:
                return artist_id
        else:
            LOG.debug('Artist not found on Spotify: ' + artist_name)
            return ''
    LOG.debug('No genre found for ' + artist_name)
    return ''

def get_artist_genre(artist_name, res):
    if res:
        items = res['artists']['items']
        if items:
            artist = items[0]
            genres = artist['genres']
            if genres:
                return genres
            # The following code has no effect since 
            # albumns don't have genre as well
            # else:
            #     artist_id = artist['id']
            #     genre = infer_genre(artist_id)
            #     if genre:
            #         return genre
        else:
            LOG.debug('No artist on Spotify: ' + artist_name)
            return []
    LOG.debug('No genre found for ' + artist_name)
    return []


# Given a request url, return the response of json object
def get_request(url):
    req = urllib.request.Request(url)
    res = None
    try:
        with urllib.request.urlopen(req) as response:
            if response.code == 200:
                res = json.loads(response.read().decode('utf-8'))
            else:
                LOG.info('Get request failed for' + url + ' response: ' + response.code)
    except UnicodeEncodeError:
        LOG.info('UnicodeEncodeError for ' +  url)
    except urllib.error.HTTPError:
        LOG.info('HTTPError for ' +  url)
    time.sleep(0.05)
    return res