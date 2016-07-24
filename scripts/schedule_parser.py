import logging
import os
import sys
import urllib.request
import argparse
import json
from bs4 import BeautifulSoup

FORMAT = '%(asctime)-15s - %(levelname)s - %(module)20s:%(lineno)-5d - %(message)s'
logging.basicConfig(stream=sys.stdout, level=logging.INFO, format=FORMAT)
LOG = logging.getLogger(__name__)


def get_html(url):
    LOG.info('Grabbing html from URL: {}'.format(url))
    with urllib.request.urlopen(url) as response:
        html = response.read()
        return html


def get_locations(header):
    columns = header.find_all('th')
    cols = [column.text.strip() for column in columns]
    return [col for col in cols if col]


def parse_schedule_table(html):
    LOG.info('Parsing schedule table...')
    soup = BeautifulSoup(html, 'html.parser')
    table = soup.find('table').find_all('tr')
    table_header = get_locations(table[0])
    table_body = table[1].find_all('td')
    LOG.debug('HEADERS: {}'.format(table_header))
    LOG.debug('BODY:\n\n{}'.format(table_body))
    return table_header, table_body


def get_events_from_lineup(lineup):
    data = []
    events = lineup.find_all('div', class_='ds-event-box')
    for event in events:
        element = event.find('a', href=True)
        artist_page = element['href']
        artist = element.text.strip()
        time = event.find('span', class_='ds-time-range').text.strip().split(sep='-', maxsplit=2)
        data.append({
            'event_name': artist,
            'event_page': "http://lineup.sfoutsidelands.com{}".format(artist_page),
            'start_time': time[0].strip(),
            'end_time': time[1].strip()
        })
    return data


def construct_json(locations, lineups):
    data = {}
    for location, lineup in zip(locations, lineups):
        data[location] = get_events_from_lineup(lineup)
    return data


def write_json_file(filename, filedir, data):
    path = os.path.expanduser(filedir)
    if not os.path.exists(path):
        LOG.info("Folder does not exist. Creating folder: %s", path)
        os.makedirs(path)
    with open(os.path.join(path, filename + '.json'), 'w') as f:
        json.dump(data, f)


def parse_args():
    LOG.info('Parsing Args')
    parser = argparse.ArgumentParser(description='Scrape Outside Lands website for schedule information')
    parser.add_argument('-d', '--output-dir', type=str, help='Output directory for JSON data')
    parser.add_argument('-n', '--name', type=str, help='Name of JSON file')
    return parser.parse_args()


def main(args):
    base_url = 'http://lineup.sfoutsidelands.com/events/2016/08/'
    urls = ['05', '06', '07']
    for url in urls:
        html = get_html("{}{}".format(base_url, url))
        locations, lineups = parse_schedule_table(html)
        data = construct_json(locations, lineups)
        write_json_file("{}-{}".format(args.name, url), args.output_dir, data)

if __name__ == "__main__":
    main(parse_args())
