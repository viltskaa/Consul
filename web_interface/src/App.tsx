import './App.css'
import React, {useEffect, useState} from "react";
import {
    Avatar,
    Button,
    Collapse,
    Divider,
    Flex,
    Form,
    FormProps,
    Input,
    List,
    Modal,
    Popconfirm,
    Result,
    Select
} from "antd";
import {DeleteOutlined} from "@ant-design/icons"
import axios, {AxiosResponse} from "axios"

type Market = {
    id: string;
    name: string;
    type: "WB" | "OZON" | "Yandex";
    wb_apikey: string | undefined;
    ozon_apikey: string | undefined;
    ozon_clientId: string | undefined;
    ozon_performanceClientId: string | undefined;
    ozon_performanceClientSecret: string | undefined;
    yandex_auth: string | undefined;
    yandex_campaignId: string | undefined;
    yandex_businessId: string | undefined;
}

type ReportForm = {
    market: string;
    year: number;
    month: number;
}

const months = [
    'Январь', 'Февраль', 'Март',
    'Апрель', 'Май', 'Июнь',
    'Июль', 'Август', 'Сентябрь',
    'Октябрь', 'Ноябрь', 'Декабрь'
]

const str2hash = (value: string): number => {
    let hash = 0, i, chr;
    if (value.length === 0) return hash;

    for (i = 0; i < value.length; i++) {
        chr = value.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}

const loadMarkets = (): Array<Market> => {
    const raw_value = localStorage.getItem("markets")
    if (raw_value)
        return JSON.parse(raw_value) as Array<Market>
    return []
}

const saveMarkets = (markets: Array<Market>) => {
    const json = JSON.stringify(markets)
    localStorage.setItem("markets", json)
}

const App = (): React.ReactElement => {
    const [form] = Form.useForm<Market>()

    const [href, setHref] = useState<string>("")
    const [download, setDownload] = useState<string>("")

    const [modalMarketsOpen, setModalMarketsOpen] = useState<boolean>(false)
    const [markets, setMarkets] = useState<Array<Market>>(loadMarkets())
    const [marketType, setMarketType] = useState<"WB" | "OZON" | "Yandex" | "">("")

    const [modalReportOpen, setModalReportOpen] = useState<boolean>(false)
    const [loading, setLoading] = useState<boolean>(false)
    const [success, setSuccess] = useState<boolean | null>(null)

    const onFinishAddMarket: FormProps<Market>['onFinish'] = (values) => {
        const currentDate = (new Date()).toJSON()
        setMarkets(old => [...old, {
            ...values,
            id: str2hash(values.name + currentDate).toString()
        }])
    };

    const onMarketTypeSelect = (value: "WB" | "OZON" | "Yandex") => setMarketType(value)

    const deleteMarket = (id: string) => setMarkets(old => old.filter(x => x.id !== id))

    useEffect(() => {
        saveMarkets(markets)
    }, [markets])

    useEffect(() => {
        if (success === null && href.length > 0) {
            window.URL.revokeObjectURL(href)
        }
    }, [success])

    const onFinishReport: FormProps<ReportForm>['onFinish'] = ({market: marketId, year, month}) => {
        const market = markets.find(x => x.id === marketId)
        if (market === undefined) return;

        (async () => {
            setLoading(true);
            const baseUrl = "http://localhost:8080";
            let result: AxiosResponse | null = null;

            try {
                switch (market.type) {
                    case "Yandex":
                        result = await axios.post(`${baseUrl}/yandexReport/getExcel`, {
                            auth: market.yandex_auth,
                            campaignId: market.yandex_campaignId,
                            year,
                            month,
                            businessId: market.yandex_businessId,
                            placementPrograms: ["FBS"]
                        }, {responseType: 'blob'})
                        break;
                    case "OZON":
                        result = await axios.post(`${baseUrl}/ozonReport/getExcel`, {
                            apiKey: market.ozon_apikey,
                            clientId: market.ozon_clientId,
                            performanceClientId: market.ozon_performanceClientId,
                            performanceClientSecret: market.ozon_performanceClientSecret,
                            year,
                            month
                        }, {responseType: 'blob'})
                        break;
                    case "WB":
                        result = await axios.post(`${baseUrl}/wbReport/getExcel`, {
                            apiKey: market.wb_apikey,
                            year,
                            month
                        }, {responseType: 'blob'})
                }
            } catch (e) {
                setSuccess(false)
            }

            if (result && result.status === 200) {
                setSuccess(true)
                const blob = new Blob(
                    [result.data],
                    {type: 'application/vnd.ms-excel'}
                );
                const url = window.URL.createObjectURL(blob)

                setHref(url)
                setDownload(`Отчет_По_${market.type}_${market.name}_от_${month}_${year}.xls`)
            }
        })().then((_: any) => setLoading(false))
    }

    return (
        <Flex justify="center" align="center" className="h-100" vertical>
            <Flex gap={"small"} vertical>
                <div className="position-relative">
                    <img className="logo" src="/ConsultLogo.png" alt=""/>
                    <p
                        className="mb-1 text-white text-end fw-bolder position-absolute end-0 bottom-0 fs-6"
                    >От Флагман</p>
                </div>
                <Button
                    className={"cst-bnt"}
                    type={"text"}
                    onClick={() => setModalMarketsOpen(true)}
                >
                    Магазины
                </Button>
                <Button
                    className={"cst-bnt"}
                    type={"text"}
                    onClick={() => setModalReportOpen(true)}
                >
                    Генерация отчета
                </Button>
            </Flex>
            <Modal
                centered
                forceRender
                title="Магазины"
                open={modalMarketsOpen}
                footer={null}
                onCancel={() => setModalMarketsOpen(false)}
            >
                <Divider/>
                <List
                    pagination={{position: "bottom", align: "center", pageSize: 3}}
                    itemLayout="horizontal"
                    size={'small'}
                    dataSource={markets}
                    locale={{
                        emptyText: <p className="text-white">Магазины не найдены</p>
                    }}
                    renderItem={(item, index) => (
                        <List.Item className="p-2 mb-2 market" key={index}>
                            <Flex className="w-100" align="center" justify="space-between">
                                <Flex align="center" gap="small">
                                    <Avatar className={item.type} shape={"square"}>{item.type}</Avatar>
                                    <span className="text-white">{item.name}</span>
                                </Flex>
                                <Popconfirm
                                    title="Удалить магазин?"
                                    onConfirm={() => deleteMarket(item.id)}
                                    okText="Да"
                                    cancelText="Нет"
                                >
                                    <Button className="market-delete" type="text" icon={<DeleteOutlined/>}/>
                                </Popconfirm>
                            </Flex>
                        </List.Item>
                    )}
                />
                <Divider/>
                <Collapse bordered={false} items={[
                    {
                        key: '1',
                        label: <p className="m-0">Новый Магазин</p>,
                        forceRender: true,
                        children: (
                            <Form
                                form={form}
                                onFinish={onFinishAddMarket}
                                autoComplete="off"
                                layout={"vertical"}
                            >
                                <Form.Item<Market>
                                    label="Имя магазина"
                                    name="name"
                                    className="mb-2"
                                    rules={[{required: true, message: 'Введите название магазина!'}]}
                                >
                                    <Input/>
                                </Form.Item>

                                <Form.Item<Market>
                                    label="Маркетплейс"
                                    name="type"
                                    className="mb-2"
                                    rules={[{required: true, message: 'Выберите маркетплейс!'}]}
                                >
                                    <Select
                                        onChange={onMarketTypeSelect}
                                        options={[
                                            {
                                                value: "WB",
                                                label: "Вайлдберис"
                                            },
                                            {
                                                value: "OZON",
                                                label: "Озон"
                                            },
                                            {
                                                value: "Yandex",
                                                label: "Яндекс Маркет"
                                            }
                                        ]}/>
                                </Form.Item>
                                {marketType && (<Divider className={"text-white"} plain>Api Ключи</Divider>)}
                                {marketType === "WB" && (
                                    <Form.Item<Market>
                                        label="Api Ключ"
                                        name="wb_apikey"
                                        className="mb-2"
                                        rules={[{required: true, message: 'Введите ключ!'}]}
                                    >
                                        <Input/>
                                    </Form.Item>
                                )}
                                {marketType === "OZON" && (
                                    <>
                                        <Form.Item<Market>
                                            label="Api Ключ"
                                            name="ozon_apikey"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                        <Form.Item<Market>
                                            label="Client Id"
                                            name="ozon_clientId"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                        <Form.Item<Market>
                                            label="Performance Client Id"
                                            name="ozon_performanceClientId"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                        <Form.Item<Market>
                                            label="Performance Client Secret"
                                            name="ozon_performanceClientSecret"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                    </>
                                )}
                                {marketType === "Yandex" && (
                                    <>
                                        <Form.Item<Market>
                                            label="Yandex Auth"
                                            name="yandex_auth"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                        <Form.Item<Market>
                                            label="Yandex Campaign Id"
                                            name="yandex_campaignId"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                        <Form.Item<Market>
                                            label="Yandex Business Id"
                                            name="yandex_businessId"
                                            className="mb-2"
                                            rules={[{required: true, message: 'Введите ключ!'}]}
                                        >
                                            <Input/>
                                        </Form.Item>
                                    </>
                                )}

                                <Form.Item className="mt-3 w-100 mb-0">
                                    <Button className="w-100" type="primary" htmlType="submit">
                                        Добавить
                                    </Button>
                                </Form.Item>
                            </Form>
                        ),
                    }
                ]}/>
            </Modal>
            <Modal
                centered
                forceRender
                title="Отчет"
                open={modalReportOpen}
                footer={null}
                onCancel={() => setModalReportOpen(false)}
                loading={loading}
            >
                <Divider/>
                <Result
                    style={{
                        display: !success ? "none" : "block"
                    }}
                    status="success"
                    title="Отчет успешно сформирован!"
                    extra={[
                        <a
                            key={"LINK"}
                            className="ant-btn css-var-r84h text-decoration-none"
                            href={href}
                            download={download}
                        >Скачать</a>,
                        <Button
                            type="text"
                            className="text-white"
                            onClick={() => setSuccess(null)}
                        >
                            Назад
                        </Button>
                    ]}
                />
                {success === false && (
                    <Result
                        status="error"
                        title="Произошла ошибка при формировании!"
                        subTitle="Проверьте API ключи!"
                        extra={[
                            <Button key="buy" onClick={() => setSuccess(null)}>
                                Попробовать снова
                            </Button>,
                        ]}
                    />
                )}
                {success === null && (
                    <Form
                        name="report"
                        layout="vertical"
                        onFinish={onFinishReport}
                        initialValues={{
                            year: 2024,
                            month: new Date().getMonth() + 1
                        }}
                    >
                        <Form.Item<ReportForm>
                            shouldUpdate
                            name="market"
                            label="Магазин"
                            className="mb-2"
                            required
                        >
                            <Select
                                showSearch
                                options={markets.map(market => ({
                                    label: `${market.type} ${market.name}`,
                                    value: market.id
                                }))}
                                optionFilterProp="label"
                                filterSort={(optionA, optionB) =>
                                    (optionA?.label ?? '').toLowerCase().localeCompare((optionB?.label ?? '').toLowerCase())
                                }
                            />
                        </Form.Item>

                        <Form.Item<ReportForm>
                            required
                            className="mb-2"
                            name="year"
                            label="Год"
                        >
                            <Input type={"number"} min={2023}/>
                        </Form.Item>

                        <Form.Item<ReportForm>
                            required
                            shouldUpdate
                            className="mb-2"
                            name="month"
                            label="Месяц"
                        >
                            <Select options={months.map((value, index) => ({value: index + 1, label: value}))}/>
                        </Form.Item>

                        <Form.Item<ReportForm> className="w-100 mb-0">
                            <Button className="w-100" htmlType={"submit"} type={"primary"}>Создать</Button>
                        </Form.Item>
                    </Form>
                )}
            </Modal>
        </Flex>
    )
}

export default App
