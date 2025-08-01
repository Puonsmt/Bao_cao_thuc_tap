from flask import Flask, render_template, request
import clickhouse_connect

app = Flask(__name__)

# Kết nối ClickHouse
client = clickhouse_connect.get_client(
    host='localhost',
    port=8123,
    username='default',
    password='',  # hoặc 'your_password' nếu có
    database='adnlog_db'
)

@app.route('/', methods=['GET'])
def index():
    return render_template('index.html')

@app.route('/query', methods=['POST'])
def query():
    query_type = request.form.get('query_type')           # 'banner' hoặc 'campaign'
    click_or_view_raw = request.form.get('click_or_view') # 'true' hoặc 'false'
    start_date = request.form.get('start_date')
    end_date = request.form.get('end_date')

    # Chuyển đổi giá trị từ chuỗi 'true'/'false' sang số nguyên 1/0
    click_or_view = 'false' if click_or_view_raw and click_or_view_raw.lower() == 'true' else 'true'

    # Chuẩn bị câu truy vấn
    if query_type == 'banner':
        banner_id = request.form.get('bannerId')
        sql = """
            SELECT 
                bannerId,
                click_or_view,
                uniqHLL12(guid) AS estimated_user_count
            FROM banner_events
            WHERE event_date BETWEEN '{start_date}' AND '{end_date}'
              AND click_or_view = {click_or_view}
              AND bannerId = {banner_id}
            GROUP BY bannerId, click_or_view
            ORDER BY click_or_view
        """
        params = {
            'start_date': start_date,
            'end_date': end_date,
            'click_or_view': click_or_view,
            'banner_id': int(banner_id)
        }

    elif query_type == 'campaign':
        campaign_id = request.form.get('campaignId')
        sql = """
            SELECT 
                campaignId,
                click_or_view,
                uniqHLL12(guid) AS estimated_user_count
            FROM campaign_events
            WHERE event_date BETWEEN '{start_date}' AND {end_date}
              AND click_or_view = {click_or_view}
              AND campaignId = {campaign_id}
            GROUP BY campaignId, click_or_view
            ORDER BY click_or_view
        """
        params = {
            'start_date': start_date,
            'end_date': end_date,
            'click_or_view': click_or_view,
            'campaign_id': int(campaign_id)
        }

    else:
        return "Truy vấn không hợp lệ", 400

    print(sql.format(**params))

    try:
        result = client.query(sql.format(**params)).result_rows
        print("Kết quả truy vấn:")
        print(result)
    except Exception as e:
        return f"Lỗi truy vấn ClickHouse: {e}", 500

    return render_template('index.html', result=result)

if __name__ == '__main__':
    app.run(debug=True)
