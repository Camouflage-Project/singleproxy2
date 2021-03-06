import React, {useEffect} from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import axios from 'axios';

export default function AdminAlertDialog(props: AdminDialogProps) {
    const [proxies, setProxies] = React.useState<Array<Proxy>>([])
    const baseUrl = `${window.location.protocol}//${window.location.hostname}`

    const fetchProxies = () => {
        axios.get(`${baseUrl}/proxies`, {
            headers: {
                "key": props.nodeKey
            }
        })
            .then(res => setProxies((res.data as Response).proxies))
    }

    useEffect(fetchProxies, [props.open])

    return (
        <div>
            <Dialog
                open={props.open}
                onClose={props.handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
                fullWidth
                maxWidth="md"
            >
                <DialogTitle id="alert-dialog-title">Your proxies</DialogTitle>
                <DialogContent>
                    {proxies.length === 0 ? "Create your first residential proxy and then find it here!" : getTable(proxies)}
                </DialogContent>
            </Dialog>
        </div>
    );
}

const getTable = (proxies: Array<Proxy>) =>
    <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} size="small" aria-label="a dense table">
            <TableHead>
                <TableRow>
                    <TableCell align="center">Platform</TableCell>
                    <TableCell align="center">Downloaded</TableCell>
                    <TableCell align="center">Successfully Installed</TableCell>
                    <TableCell align="center">IP Address</TableCell>
                    <TableCell align="center">Last Heartbeat</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {proxies.map((proxy, i) => (
                    <TableRow
                        key={i}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                    >
                        <TableCell align="center">{proxy.platform}</TableCell>
                        <TableCell align="center">{formatLocalDateTime(proxy.created)}</TableCell>
                        <TableCell align="center">{proxy.registered.toString()}</TableCell>
                        <TableCell align="center">{formatIpAddress(proxy.ipAddress)}</TableCell>
                        <TableCell align="center">{formatLocalDateTime(proxy.lastHeartbeat)}</TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    </TableContainer>

const formatIpAddress = (ip: String) => {
    if (ip) return ip
    else return "-"
}

const formatLocalDateTime = (ldt: LocalDateTime) => {
    if (ldt)
        return `${ldt.year}-${fmt(ldt.month)}-${fmt(ldt.day)}-T${fmt(ldt.hour)}:${fmt(ldt.minute)}:${fmt(ldt.second)}`
    else return "-"
}

const fmt = (temporalElement: BigInt) => {
    if ((Number(temporalElement) / 10) < 1) return `0${temporalElement}`
    else return temporalElement.toString()
}

interface AdminDialogProps {
    open: boolean,
    handleClose: () => void
    nodeKey: string
}

interface Response {
    proxies: Array<Proxy>
}

interface Proxy {
    platform: String,
    registered: Boolean,
    ipAddress: String,
    lastHeartbeat: LocalDateTime,
    created: LocalDateTime
}

interface LocalDateTime {
    year: BigInt,
    month: BigInt,
    day: BigInt,
    hour: BigInt,
    minute: BigInt,
    second: BigInt,
    nanosecond: BigInt
}
