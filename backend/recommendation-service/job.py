from prefect import flow
from prefect.schedules import Cron
import service.trend as trend_service

@flow
def slide_trend_window():
    trend_service.slide_trend_window()

if __name__ == '__main__':
    slide_trend_window.serve(
        name='slide-trend-window',
        schedule=Cron('0 0/20 * * * ?', timezone='Asia/Seoul')
    )